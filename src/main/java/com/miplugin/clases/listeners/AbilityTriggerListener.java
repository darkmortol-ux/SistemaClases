package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;
import com.miplugin.clases.model.PlayerClaseData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AbilityTriggerListener implements Listener {

    private final ClasesPlugin plugin;

    public AbilityTriggerListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ClaseDefinicion claseDelItem = plugin.getHabilidadItemService().getClaseDelFoco(event.getItem());
        if (claseDelItem == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerClaseData data = plugin.getClaseManager().getDatos(player.getUniqueId());
        ClaseDefinicion claseActual = data.getClase();

        if (claseActual == null) {
            player.sendMessage(ChatColor.RED + "Todavía no has elegido una clase. Usa /clase elegir <nombre>.");
            return;
        }

        if (claseDelItem != claseActual) {
            player.sendMessage(ChatColor.RED + "Ese foco es de " + claseDelItem.getNombreDisplay()
                    + ", pero tu clase actual es " + claseActual.getNombreDisplay() + ".");
            return;
        }

        if (data.enCooldown()) {
            player.sendMessage(ChatColor.RED + "Habilidad en cooldown: " + data.segundosRestantes() + "s restantes.");
            return;
        }

        plugin.getHabilidadRegistry().get(claseActual).ejecutar(player);
        plugin.getClaseManager().iniciarCooldown(player);
        player.sendMessage(ChatColor.AQUA + "¡" + plugin.getHabilidadRegistry().get(claseActual).getNombreDisplay() + " activada!");
    }
}
