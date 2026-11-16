package com.miplugin.clases.missions;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AldeanoListener implements Listener {

    private final ClasesPlugin plugin;

    public AldeanoListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!plugin.getAldeanoService().esAldeano(event.getRightClicked())) return;

        // Cancelamos siempre para que no se abra el menú de comercio del aldeano.
        event.setCancelled(true);

        // El evento llega una vez por cada mano; solo actuamos con la principal.
        if (event.getHand() != EquipmentSlot.HAND) return;

        plugin.getMissionManager().interactuar(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (plugin.getAldeanoService().esAldeano(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
