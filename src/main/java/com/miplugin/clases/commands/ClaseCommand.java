package com.miplugin.clases.commands;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;
import com.miplugin.clases.model.PlayerClaseData;
import org.bukkit.ChatColor;
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
            default -> enviarAyuda(player);
        }
        return true;
    }

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

    private void enviarAyuda(Player player) {
        player.sendMessage(ChatColor.YELLOW + "/clase elegir <clase>" + ChatColor.WHITE + " - Elige o cambia de clase");
        player.sendMessage(ChatColor.YELLOW + "/clase info" + ChatColor.WHITE + " - Ver tu clase y cooldown actual");
    }

    private String nombresClases() {
        return Arrays.stream(ClaseDefinicion.values())
                .map(c -> c.name().toLowerCase())
                .collect(Collectors.joining("|"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("elegir", "info", "reset", "estrella").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("elegir")) {
            return Arrays.stream(ClaseDefinicion.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
