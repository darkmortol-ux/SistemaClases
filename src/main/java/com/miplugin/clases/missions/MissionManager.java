package com.miplugin.clases.missions;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MissionManager {

    private static final String PREFIJO = ChatColor.LIGHT_PURPLE + "[Aldeano] " + ChatColor.GRAY;

    private final ClasesPlugin plugin;
    private final File configFile;
    private final File dataFile;
    private final Map<Integer, Mission> misiones = new LinkedHashMap<>();
    private final Map<UUID, PlayerMissionData> datos = new HashMap<>();
    private final Map<UUID, BossBar> barras = new HashMap<>();
    private final Random random = new Random();
    private boolean dirty = false;

    private long cooldownMillis = 12L * 3600_000L;
    private double probabilidadEsencia = 0.40;
    private int esenciasMin = 1;
    private int esenciasMax = 2;

    public MissionManager(ClasesPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "misiones.yml");
        this.dataFile = new File(plugin.getDataFolder(), "misiones-datos.yml");
    }

    // ------------------------------------------------------------------ config

    public void cargarConfig() {
        misiones.clear();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(configFile);

        cooldownMillis = (long) (yaml.getDouble("cooldown-horas", 12.0) * 3600_000L);
        probabilidadEsencia = Math.max(0.0, Math.min(1.0, yaml.getDouble("probabilidad-esencia", 0.40)));
        esenciasMin = Math.max(1, yaml.getInt("esencias-min", 1));
        esenciasMax = Math.max(esenciasMin, yaml.getInt("esencias-max", 2));

        for (Map<?, ?> raw : yaml.getMapList("misiones")) {
            try {
                int id = ((Number) raw.get("id")).intValue();
                Mission.Dificultad dificultad = Mission.Dificultad.valueOf(String.valueOf(raw.get("dificultad")).toUpperCase());
                Mission.Tipo tipo = Mission.Tipo.valueOf(String.valueOf(raw.get("tipo")).toUpperCase());
                String objetivo = String.valueOf(raw.get("objetivo")).toUpperCase();
                int cantidad = ((Number) raw.get("cantidad")).intValue();
                if (cantidad < 1) throw new IllegalArgumentException("la cantidad debe ser 1 o más");
                validarObjetivo(tipo, objetivo);
                misiones.put(id, new Mission(id, dificultad, tipo, objetivo, cantidad));
            } catch (Exception e) {
                plugin.getLogger().warning("Misión inválida en misiones.yml (" + raw + "): " + e.getMessage());
            }
        }
        plugin.getLogger().info(misiones.size() + " misiones del aldeano cargadas.");
    }

    private void validarObjetivo(Mission.Tipo tipo, String objetivo) {
        if (tipo == Mission.Tipo.KILL) {
            boolean existe = Arrays.stream(EntityType.values()).anyMatch(t -> t.name().equals(objetivo));
            if (!existe) throw new IllegalArgumentException("mob desconocido: " + objetivo);
        } else if (Material.matchMaterial(objetivo) == null) {
            throw new IllegalArgumentException("material desconocido: " + objetivo);
        }
    }

    // ------------------------------------------------------------------ datos

    public PlayerMissionData getDatos(UUID uuid) {
        return datos.computeIfAbsent(uuid, id -> new PlayerMissionData());
    }

    public Mission getMisionActiva(UUID uuid) {
        PlayerMissionData d = datos.get(uuid);
        if (d == null || d.getMisionActiva() == null) return null;
        return misiones.get(d.getMisionActiva());
    }

    public void cargarDatos() {
        datos.clear();
        if (!dataFile.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection seccion = yaml.getConfigurationSection("jugadores");
        if (seccion == null) return;

        for (String key : seccion.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                PlayerMissionData d = new PlayerMissionData();
                int activa = seccion.getInt(key + ".mision-activa", 0);
                d.setMisionActiva(activa > 0 ? activa : null);
                d.setProgreso(seccion.getInt(key + ".progreso", 0));
                d.setCooldownHasta(seccion.getLong(key + ".cooldown-hasta", 0L));
                d.setUltimaMision(seccion.getInt(key + ".ultima-mision", 0));
                datos.put(uuid, d);
            } catch (Exception e) {
                plugin.getLogger().warning("No se pudo cargar datos de misión para " + key + ": " + e.getMessage());
            }
        }
    }

    public void guardarDatos() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerMissionData> entry : datos.entrySet()) {
            PlayerMissionData d = entry.getValue();
            String path = "jugadores." + entry.getKey();
            yaml.set(path + ".mision-activa", d.getMisionActiva() == null ? 0 : d.getMisionActiva());
            yaml.set(path + ".progreso", d.getProgreso());
            yaml.set(path + ".cooldown-hasta", d.getCooldownHasta());
            yaml.set(path + ".ultima-mision", d.getUltimaMision());
        }

        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            yaml.save(dataFile);
            dirty = false;
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar misiones-datos.yml: " + e.getMessage());
        }
    }

    public void guardarSiHaceFalta() {
        if (dirty) guardarDatos();
    }

    // ------------------------------------------------------------------ interacción con el aldeano

    public void interactuar(Player player) {
        PlayerMissionData data = getDatos(player.getUniqueId());

        if (data.getMisionActiva() == null) {
            if (data.enCooldown()) {
                msg(player, "Aún no tengo otra misión para ti. Vuelve en "
                        + formatearTiempo(data.segundosRestantes()) + ".");
                return;
            }
            asignarMision(player, data);
            return;
        }

        Mission mision = misiones.get(data.getMisionActiva());
        if (mision == null) {
            data.setMisionActiva(null);
            data.setProgreso(0);
            dirty = true;
            quitarBarra(player);
            msg(player, "Tu misión anterior ya no existe. Habla conmigo otra vez para recibir una nueva.");
            return;
        }

        if (mision.tipo() == Mission.Tipo.ENTREGAR) {
            Material material = Material.matchMaterial(mision.objetivo());
            if (material == null || contarItems(player, material) < mision.cantidad()) {
                msg(player, "Tu misión: " + ChatColor.WHITE + mision.descripcion()
                        + ChatColor.GRAY + ". Aún no tienes lo que pido.");
                return;
            }
            quitarItems(player, material, mision.cantidad());
            data.setProgreso(mision.cantidad());
        }

        if (data.getProgreso() >= mision.cantidad()) {
            completarMision(player, data);
        } else {
            msg(player, "Tu misión: " + ChatColor.WHITE + mision.descripcion()
                    + ChatColor.GRAY + " (" + data.getProgreso() + "/" + mision.cantidad() + ")");
        }
    }

    private void asignarMision(Player player, PlayerMissionData data) {
        if (misiones.isEmpty()) {
            msg(player, "No tengo misiones disponibles por ahora.");
            return;
        }

        List<Mission> lista = new ArrayList<>(misiones.values());
        Mission elegida;
        do {
            elegida = lista.get(random.nextInt(lista.size()));
        } while (lista.size() > 1 && elegida.id() == data.getUltimaMision());

        data.setMisionActiva(elegida.id());
        data.setProgreso(0);
        data.setUltimaMision(elegida.id());
        dirty = true;

        mostrarBarra(player, elegida, 0);

        ChatColor color = elegida.dificultad() == Mission.Dificultad.EXTREMA ? ChatColor.RED : ChatColor.GOLD;
        msg(player, "Tu misión " + color + "(" + elegida.dificultad().name() + ")" + ChatColor.GRAY + ": "
                + ChatColor.WHITE + elegida.descripcion() + ChatColor.GRAY + ".");
        msg(player, "Vuelve conmigo cuando la completes para recibir tu recompensa.");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        guardarDatos();
    }

    private void completarMision(Player player, PlayerMissionData data) {
        data.setMisionActiva(null);
        data.setProgreso(0);
        data.setCooldownHasta(System.currentTimeMillis() + cooldownMillis);
        dirty = true;
        quitarBarra(player);

        player.sendMessage(PREFIJO + ChatColor.GREEN + "¡Misión completada!");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        darRecompensa(player);
        guardarDatos();
    }

    private void darRecompensa(Player player) {
        if (random.nextDouble() >= probabilidadEsencia) {
            msg(player, "Esta vez no tengo esencias para ti. Vuelve más tarde.");
            return;
        }

        int cantidad = esenciasMin + random.nextInt(esenciasMax - esenciasMin + 1);
        for (int i = 0; i < cantidad; i++) {
            ItemStack esencia = plugin.getEsenciaService().crearAleatoria();
            var sobrante = player.getInventory().addItem(esencia);
            sobrante.values().forEach(item ->
                    player.getWorld().dropItemNaturally(player.getLocation(), item));
            player.sendMessage(PREFIJO + ChatColor.LIGHT_PURPLE + "Recibiste: "
                    + esencia.getItemMeta().getDisplayName());
        }
    }

    // ------------------------------------------------------------------ progreso (KILL / MINE)

    public void registrarProgreso(Player player, Mission.Tipo tipo, String nombreObjetivo) {
        PlayerMissionData data = datos.get(player.getUniqueId());
        if (data == null || data.getMisionActiva() == null) return;

        Mission mision = misiones.get(data.getMisionActiva());
        if (mision == null || mision.tipo() != tipo) return;
        if (!coincide(mision, nombreObjetivo)) return;
        if (data.getProgreso() >= mision.cantidad()) return;

        data.setProgreso(data.getProgreso() + 1);
        dirty = true;
        actualizarBarra(player, mision, data.getProgreso());

        if (data.getProgreso() >= mision.cantidad()) {
            player.sendMessage(PREFIJO + ChatColor.GREEN
                    + "¡Objetivo cumplido! Vuelve con el aldeano para reclamar tu recompensa.");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        }
    }

    private boolean coincide(Mission mision, String nombre) {
        if (mision.tipo() == Mission.Tipo.KILL) {
            return nombre.equals(mision.objetivo());
        }
        // MINE: DIAMOND_ORE también cuenta DEEPSLATE_DIAMOND_ORE
        return nombre.equals(mision.objetivo()) || nombre.endsWith("_" + mision.objetivo());
    }

    // ------------------------------------------------------------------ comandos de jugador / admin

    public boolean abandonar(Player player) {
        PlayerMissionData data = getDatos(player.getUniqueId());
        if (data.getMisionActiva() == null) return false;

        data.setMisionActiva(null);
        data.setProgreso(0);
        data.setCooldownHasta(System.currentTimeMillis() + cooldownMillis);
        dirty = true;
        quitarBarra(player);
        guardarDatos();
        return true;
    }

    public void reiniciar(UUID uuid) {
        PlayerMissionData data = getDatos(uuid);
        data.setMisionActiva(null);
        data.setProgreso(0);
        data.setCooldownHasta(0);
        dirty = true;

        Player online = Bukkit.getPlayer(uuid);
        if (online != null) quitarBarra(online);
        guardarDatos();
    }

    public String formatearTiempo(long segundos) {
        long h = segundos / 3600;
        long m = (segundos % 3600) / 60;
        long s = segundos % 60;
        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m " + s + "s";
        return s + "s";
    }

    // ------------------------------------------------------------------ inventario (solo ítems sin meta: no se entregan ítems custom)

    private int contarItems(Player player, Material material) {
        int total = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && item.getType() == material && !item.hasItemMeta()) {
                total += item.getAmount();
            }
        }
        return total;
    }

    private void quitarItems(Player player, Material material, int cantidad) {
        int restante = cantidad;
        ItemStack[] contenido = player.getInventory().getStorageContents();
        for (int i = 0; i < contenido.length && restante > 0; i++) {
            ItemStack item = contenido[i];
            if (item == null || item.getType() != material || item.hasItemMeta()) continue;

            int quitar = Math.min(restante, item.getAmount());
            if (quitar >= item.getAmount()) {
                contenido[i] = null;
            } else {
                item.setAmount(item.getAmount() - quitar);
            }
            restante -= quitar;
        }
        player.getInventory().setStorageContents(contenido);
    }

    // ------------------------------------------------------------------ bossbar

    public void restaurarBarra(Player player) {
        PlayerMissionData data = datos.get(player.getUniqueId());
        if (data == null || data.getMisionActiva() == null) return;
        Mission mision = misiones.get(data.getMisionActiva());
        if (mision == null) return;

        mostrarBarra(player, mision, data.getProgreso());
        actualizarBarra(player, mision, data.getProgreso());
    }

    private void mostrarBarra(Player player, Mission mision, int progreso) {
        quitarBarra(player);
        BarColor color = mision.dificultad() == Mission.Dificultad.EXTREMA ? BarColor.RED : BarColor.PURPLE;
        BossBar barra = Bukkit.createBossBar(titulo(mision, progreso), color, BarStyle.SEGMENTED_10);
        barra.setProgress(fraccion(mision, progreso));
        barra.addPlayer(player);
        barras.put(player.getUniqueId(), barra);
    }

    private void actualizarBarra(Player player, Mission mision, int progreso) {
        BossBar barra = barras.get(player.getUniqueId());
        if (barra == null) return;

        if (mision.tipo() != Mission.Tipo.ENTREGAR && progreso >= mision.cantidad()) {
            barra.setColor(BarColor.GREEN);
            barra.setTitle(ChatColor.GREEN + "¡Completada! Vuelve con el aldeano");
            barra.setProgress(1.0);
        } else {
            barra.setTitle(titulo(mision, progreso));
            barra.setProgress(fraccion(mision, progreso));
        }
    }

    public void quitarBarra(Player player) {
        BossBar barra = barras.remove(player.getUniqueId());
        if (barra != null) {
            barra.removeAll();
        }
    }

    public void quitarTodasLasBarras() {
        for (BossBar barra : barras.values()) {
            barra.removeAll();
        }
        barras.clear();
    }

    private String titulo(Mission mision, int progreso) {
        String prefijo = mision.dificultad() == Mission.Dificultad.EXTREMA
                ? ChatColor.RED + "[Extrema] " : ChatColor.GOLD + "[Difícil] ";
        if (mision.tipo() == Mission.Tipo.ENTREGAR) {
            return prefijo + ChatColor.WHITE + mision.descripcion() + ChatColor.GRAY + " (llévalo al aldeano)";
        }
        return prefijo + ChatColor.WHITE + mision.descripcion() + ChatColor.GRAY
                + " (" + Math.min(progreso, mision.cantidad()) + "/" + mision.cantidad() + ")";
    }

    private double fraccion(Mission mision, int progreso) {
        if (mision.tipo() == Mission.Tipo.ENTREGAR) return 0.0;
        return Math.max(0.0, Math.min(1.0, (double) progreso / mision.cantidad()));
    }

    private void msg(Player player, String texto) {
        player.sendMessage(PREFIJO + texto);
    }
}
