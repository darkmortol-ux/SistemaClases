package com.miplugin.clases.commands;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.missions.Mission;
import com.miplugin.clases.model.ClaseDefinicion;
import com.miplugin.clases.model.PlayerClaseData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClaseCommand implements CommandExecutor, TabCompleter {

    private final ClasesPlugin plugin;

    public ClaseCommand(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede usarse en el juego.");
            return true;
        }

        if (!player.hasPermission("clases.usar")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar el sistema de clases.");
            return true;
        }

        if (args.length == 0) {
            enviarAyuda(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "elegir" -> handleElegir(player, args);
            case "info" -> handleInfo(player);
            case "reset" -> handleReset(player, args);
            case "estrella" -> handleEstrella(player, args);
            case "esencia" -> handleEsencia(player, args);
            case "aldeano" -> handleAldeano(player, args);
            case "mision" -> handleMision(player, args);
            default -> enviarAyuda(player);
        }
        return true;
    }

    // ------------------------------------------------------------------ clases

    private void handleElegir(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /clase elegir <" + nombresClases() + ">");
            return;
        }

        ClaseDefinicion clase;
        try {
            clase = ClaseDefinicion.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Clase inválida. Opciones: " + nombresClases());
            return;
        }

        if (!plugin.getClaseManager().puedeElegirClase(player)) {
            int slotEstrella = plugin.getEstrellaCambioService().buscarEnHotbar(player);
            if (slotEstrella == -1) {
                player.sendMessage(ChatColor.RED + "Ya usaste tu límite de elección y cambio de clase.");
                player.sendMessage(ChatColor.GRAY + "Necesitas una §d★ Estrella del Cambio §7en tu hotbar para cambiar de nuevo.");
                return;
            }

            plugin.getEstrellaCambioService().consumir(player, slotEstrella);
            plugin.getClaseManager().otorgarCambioExtra(player);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usaste una ★ Estrella del Cambio.");
        }

        plugin.getClaseManager().elegirClase(player, clase);

        boolean yaLoTiene = Arrays.stream(player.getInventory().getContents())
                .anyMatch(item -> plugin.getHabilidadItemService().getClaseDelFoco(item) == clase);

        if (!yaLoTiene) {
            ItemStack foco = plugin.getHabilidadItemService().crear(clase);
            var leftover = player.getInventory().addItem(foco);
            if (!leftover.isEmpty()) {
                leftover.values().forEach(item ->
                        player.getWorld().dropItemNaturally(player.getLocation(), item));
                player.sendMessage(ChatColor.YELLOW + "Tu inventario estaba lleno, el foco se dejó caer en el suelo.");
            }
        }
    }

    private void handleInfo(Player player) {
        PlayerClaseData data = plugin.getClaseManager().getDatos(player.getUniqueId());
        ClaseDefinicion clase = data.getClase();

        if (clase == null) {
            player.sendMessage(ChatColor.YELLOW + "No has elegido una clase todavía. Usa /clase elegir <nombre>.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "Clase actual: " + ChatColor.WHITE + clase.getNombreDisplay());
        player.sendMessage(ChatColor.AQUA + "Habilidad: " + ChatColor.WHITE
                + plugin.getHabilidadRegistry().get(clase).getNombreDisplay()
                + ChatColor.GRAY + " (cooldown: " + clase.getCooldownSegundos() + "s)");

        if (data.enCooldown()) {
            player.sendMessage(ChatColor.RED + "En cooldown: " + data.segundosRestantes() + "s restantes.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Habilidad lista para usar.");
        }

        if (data.puedeElegirOCambiar()) {
            player.sendMessage(ChatColor.GRAY + "Cambios de clase disponibles: 1");
        } else {
            player.sendMessage(ChatColor.GRAY + "Ya no puedes cambiar de clase.");
        }
    }

    private void handleReset(Player player, String[] args) {
        if (!player.hasPermission("clases.admin")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar esto.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /clase reset <jugador>");
            return;
        }

        Player objetivo = player.getServer().getPlayer(args[1]);
        if (objetivo == null) {
            player.sendMessage(ChatColor.RED + "Ese jugador no está conectado.");
            return;
        }

        plugin.getClaseManager().resetearClase(objetivo);
        player.sendMessage(ChatColor.GREEN + "Clase de " + objetivo.getName() + " reseteada.");
    }

    // ------------------------------------------------------------------ objetos (admin)

    private void handleEstrella(Player sender, String[] args) {
        if (!sender.hasPermission("clases.admin")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar esto.");
            return;
        }
        if (args.length < 3 || !args[1].equalsIgnoreCase("dar")) {
            sender.sendMessage(ChatColor.RED + "Uso: /clase estrella dar <jugador> [cantidad]");
            return;
        }

        Player objetivo = sender.getServer().getPlayer(args[2]);
        if (objetivo == null) {
            sender.sendMessage(ChatColor.RED + "Ese jugador no está conectado.");
            return;
        }

        int cantidad = 1;
        if (args.length >= 4) {
            try {
                cantidad = Math.max(1, Integer.parseInt(args[3]));
            } catch (NumberFormatException ignored) {}
        }

        ItemStack estrella = plugin.getEstrellaCambioService().crear();
        estrella.setAmount(cantidad);

        var leftover = objetivo.getInventory().addItem(estrella);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(item ->
                    objetivo.getWorld().dropItemNaturally(objetivo.getLocation(), item));
        }

        sender.sendMessage(ChatColor.GREEN + "Le diste " + cantidad + " ★ Estrella(s) del Cambio a " + objetivo.getName() + ".");
        objetivo.sendMessage(ChatColor.LIGHT_PURPLE + "Recibiste " + cantidad + " ★ Estrella(s) del Cambio.");
    }

    private void handleEsencia(Player sender, String[] args) {
        if (!sender.hasPermission("clases.admin")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar esto.");
            return;
        }
        if (args.length < 4 || !args[1].equalsIgnoreCase("dar")) {
            sender.sendMessage(ChatColor.RED + "Uso: /clase esencia dar <jugador> <" + nombresClases() + "|aleatoria> [cantidad]");
            return;
        }

        Player objetivo = sender.getServer().getPlayer(args[2]);
        if (objetivo == null) {
            sender.sendMessage(ChatColor.RED + "Ese jugador no está conectado.");
            return;
        }

        int cantidad = 1;
        if (args.length >= 5) {
            try {
                cantidad = Math.max(1, Integer.parseInt(args[4]));
            } catch (NumberFormatException ignored) {}
        }

        ItemStack esencia;
        String etiqueta;

        if (args[3].equalsIgnoreCase("aleatoria")) {
            esencia = plugin.getEsenciaService().crearAleatoria();
            etiqueta = "aleatoria";
        } else {
            ClaseDefinicion clase;
            try {
                clase = ClaseDefinicion.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Clase inválida. Opciones: " + nombresClases() + "|aleatoria");
                return;
            }
            esencia = plugin.getEsenciaService().crear(clase);
            etiqueta = clase.getNombreDisplay();
        }

        esencia.setAmount(cantidad);

        var leftover = objetivo.getInventory().addItem(esencia);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(item ->
                    objetivo.getWorld().dropItemNaturally(objetivo.getLocation(), item));
        }

        sender.sendMessage(ChatColor.GREEN + "Le diste " + cantidad + " esencia(s) de " + etiqueta + " a " + objetivo.getName() + ".");
        objetivo.sendMessage(ChatColor.LIGHT_PURPLE + "Recibiste " + cantidad + " esencia(s) de " + etiqueta + ".");
    }

    // ------------------------------------------------------------------ aldeano de misiones

    private void handleAldeano(Player player, String[] args) {
        if (!player.hasPermission("clases.admin")) {
            player.sendMessage(ChatColor.RED + "No tienes permiso para usar esto.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /clase aldeano <colocar|quitar>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "colocar" -> {
                Location ubicacion = player.getLocation();
                ubicacion.setYaw(ubicacion.getYaw() + 180f); // mira hacia ti
                ubicacion.setPitch(0f);
                plugin.getAldeanoService().crear(ubicacion);
                player.sendMessage(ChatColor.GREEN + "Aldeano de las Esencias colocado en tu posición.");
            }
            case "quitar" -> {
                if (plugin.getAldeanoService().quitarCercano(player.getLocation(), 10)) {
                    player.sendMessage(ChatColor.GREEN + "Aldeano eliminado.");
                } else {
                    player.sendMessage(ChatColor.RED + "No hay ningún Aldeano de las Esencias a menos de 10 bloques.");
                }
            }
            default -> player.sendMessage(ChatColor.RED + "Uso: /clase aldeano <colocar|quitar>");
        }
    }

    private void handleMision(Player player, String[] args) {
        String sub = args.length >= 2 ? args[1].toLowerCase() : "info";

        switch (sub) {
            case "info" -> handleMisionInfo(player);
            case "abandonar" -> {
                if (plugin.getMissionManager().abandonar(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Abandonaste tu misión. El cooldown para pedir otra ya está corriendo.");
                } else {
                    player.sendMessage(ChatColor.RED + "No tienes una misión activa.");
                }
            }
            case "reiniciar" -> {
                if (!player.hasPermission("clases.admin")) {
                    player.sendMessage(ChatColor.RED + "No tienes permiso para usar esto.");
                    return;
                }
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Uso: /clase mision reiniciar <jugador>");
                    return;
                }
                Player objetivo = player.getServer().getPlayer(args[2]);
                if (objetivo == null) {
                    player.sendMessage(ChatColor.RED + "Ese jugador no está conectado.");
                    return;
                }
                plugin.getMissionManager().reiniciar(objetivo.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Misión y cooldown de " + objetivo.getName() + " reiniciados.");
            }
            case "recargar" -> {
                if (!player.hasPermission("clases.admin")) {
                    player.sendMessage(ChatColor.RED + "No tienes permiso para usar esto.");
                    return;
                }
                plugin.getMissionManager().cargarConfig();
                player.sendMessage(ChatColor.GREEN + "misiones.yml recargado.");
            }
            default -> player.sendMessage(ChatColor.RED + "Uso: /clase mision [info|abandonar|reiniciar <jugador>|recargar]");
        }
    }

    private void handleMisionInfo(Player player) {
        var manager = plugin.getMissionManager();
        var data = manager.getDatos(player.getUniqueId());
        Mission mision = manager.getMisionActiva(player.getUniqueId());

        if (mision != null) {
            player.sendMessage(ChatColor.AQUA + "Misión activa: " + ChatColor.WHITE + mision.descripcion());
            if (mision.tipo() == Mission.Tipo.ENTREGAR) {
                player.sendMessage(ChatColor.GRAY + "Llévalo al Aldeano de las Esencias.");
            } else {
                player.sendMessage(ChatColor.GRAY + "Progreso: " + data.getProgreso() + "/" + mision.cantidad());
            }
        } else if (data.enCooldown()) {
            player.sendMessage(ChatColor.YELLOW + "Podrás pedir otra misión en " + manager.formatearTiempo(data.segundosRestantes()) + ".");
        } else {
            player.sendMessage(ChatColor.GREEN + "Puedes pedir una misión al Aldeano de las Esencias.");
        }
    }

    // ------------------------------------------------------------------ ayuda y autocompletado

    private void enviarAyuda(Player player) {
        player.sendMessage(ChatColor.YELLOW + "/clase elegir <clase>" + ChatColor.WHITE + " - Elige o cambia de clase");
        player.sendMessage(ChatColor.YELLOW + "/clase info" + ChatColor.WHITE + " - Ver tu clase y cooldown actual");
        player.sendMessage(ChatColor.YELLOW + "/clase mision" + ChatColor.WHITE + " - Ver tu misión del aldeano");
    }

    private String nombresClases() {
        return Arrays.stream(ClaseDefinicion.values())
                .map(c -> c.name().toLowerCase())
                .collect(Collectors.joining("|"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filtrar(List.of("elegir", "info", "mision", "reset", "estrella", "esencia", "aldeano"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("elegir")) {
            return filtrar(nombresClasesLista(), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("aldeano")) {
            return filtrar(List.of("colocar", "quitar"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("mision")) {
            return filtrar(List.of("info", "abandonar", "reiniciar", "recargar"), args[1]);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("esencia") && args[1].equalsIgnoreCase("dar")) {
            List<String> opciones = new ArrayList<>(nombresClasesLista());
            opciones.add("aleatoria");
            return filtrar(opciones, args[3]);
        }
        return new ArrayList<>();
    }

    private List<String> nombresClasesLista() {
        return Arrays.stream(ClaseDefinicion.values())
                .map(c -> c.name().toLowerCase())
                .collect(Collectors.toList());
    }

    private List<String> filtrar(List<String> opciones, String prefijo) {
        return opciones.stream()
                .filter(s -> s.startsWith(prefijo.toLowerCase()))
                .collect(Collectors.toList());
    }
}
