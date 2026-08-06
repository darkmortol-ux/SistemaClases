package com.miplugin.clases;

import com.miplugin.clases.attributes.AttributeService;
import com.miplugin.clases.model.ClaseDefinicion;
import com.miplugin.clases.model.PlayerClaseData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaseManager {

    private final ClasesPlugin plugin;
    private final AttributeService attributeService;
    private final Map<UUID, PlayerClaseData> datos = new HashMap<>();
    private final File file;

    public ClaseManager(ClasesPlugin plugin, AttributeService attributeService) {
        this.plugin = plugin;
        this.attributeService = attributeService;
        this.file = new File(plugin.getDataFolder(), "clases.yml");
    }

    public PlayerClaseData getDatos(UUID uuid) {
        return datos.computeIfAbsent(uuid, id -> new PlayerClaseData(null, 0, 0, 0));
    }

    public boolean puedeElegirClase(Player player) {
        return getDatos(player.getUniqueId()).puedeElegirOCambiar();
    }

    public void otorgarCambioExtra(Player player) {
        getDatos(player.getUniqueId()).agregarCambioExtra();
        save();
    }

    public void elegirClase(Player player, ClaseDefinicion nuevaClase) {
        PlayerClaseData data = getDatos(player.getUniqueId());
        ClaseDefinicion claseAnterior = data.getClase();

        if (claseAnterior != null) {
            var inventario = player.getInventory();

            for (int i = 0; i < inventario.getSize(); i++) {
                var item = inventario.getItem(i);
                if (item != null && plugin.getHabilidadItemService().getClaseDelFoco(item) == claseAnterior) {
                    inventario.setItem(i, null);
                }
            }

            var offhand = inventario.getItemInOffHand();
            if (plugin.getHabilidadItemService().getClaseDelFoco(offhand) == claseAnterior) {
                inventario.setItemInOffHand(null);
            }
        }

        data.setClase(nuevaClase);
        data.setCooldownExpiraEn(0);
        data.incrementarCambios();
        attributeService.cambiarClase(player, nuevaClase);
        save();

        if (claseAnterior == null) {
            player.sendMessage(ChatColor.GREEN + "Elegiste la clase " + ChatColor.AQUA + nuevaClase.getNombreDisplay()
                    + ChatColor.GREEN + ". Pasivas activadas.");
            player.sendMessage(ChatColor.GRAY + "Podrás cambiar de clase 1 vez más en el futuro.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Cambiaste de " + ChatColor.WHITE + claseAnterior.getNombreDisplay()
                    + ChatColor.YELLOW + " a " + ChatColor.AQUA + nuevaClase.getNombreDisplay() + ChatColor.YELLOW + ".");
            player.sendMessage(ChatColor.GRAY + "Se eliminaron las pasivas y el foco de " + claseAnterior.getNombreDisplay() + ".");
            if (!data.puedeElegirOCambiar()) {
                player.sendMessage(ChatColor.RED + "Ya no podrás volver a cambiar de clase.");
            }
        }
    }

    public void reaplicarAlEntrar(Player player) {
        PlayerClaseData data = getDatos(player.getUniqueId());
        if (data.getClase() != null) {
            attributeService.aplicarModificadores(player, data.getClase());
        }
    }

    public void iniciarCooldown(Player player) {
        PlayerClaseData data = getDatos(player.getUniqueId());
        ClaseDefinicion clase = data.getClase();
        if (clase == null) return;
        data.setCooldownExpiraEn(System.currentTimeMillis() + clase.getCooldownSegundos() * 1000L);
    }

    public void resetearClase(Player player) {
        PlayerClaseData data = getDatos(player.getUniqueId());
        data.setClase(null);
        attributeService.limpiarModificadores(player);
        save();
    }

    public void load() {
        datos.clear();
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        var section = yaml.getConfigurationSection("jugadores");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String claseStr = section.getString(key + ".clase");
                ClaseDefinicion clase = claseStr != null ? ClaseDefinicion.valueOf(claseStr) : null;
                int cambios = section.getInt(key + ".cambios", 0);
                int cambiosExtra = section.getInt(key + ".cambios-extra", 0);
                datos.put(uuid, new PlayerClaseData(clase, 0, cambios, cambiosExtra));
            } catch (Exception e) {
                plugin.getLogger().warning("No se pudo cargar datos de clase para " + key + ": " + e.getMessage());
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerClaseData> entry : datos.entrySet()) {
            var data = entry.getValue();
            if (data.getClase() == null && data.getCambiosRealizados() == 0) continue;

            String path = "jugadores." + entry.getKey();
            if (data.getClase() != null) {
                yaml.set(path + ".clase", data.getClase().name());
            }
            yaml.set(path + ".cambios", data.getCambiosRealizados());
            yaml.set(path + ".cambios-extra", data.getCambiosExtra());
        }

        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar clases.yml: " + e.getMessage());
        }
    }
}
