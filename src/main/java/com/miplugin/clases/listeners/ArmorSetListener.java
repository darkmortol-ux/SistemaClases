package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorChangeEvent;

public class ArmorSetListener implements Listener {

    private final ClasesPlugin plugin;

    public ArmorSetListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        var clase = plugin.getClaseManager().getDatos(player.getUniqueId()).getClase();

        plugin.getServer().getScheduler().runTask(plugin, () ->
                plugin.getArmorMitigationService().actualizar(player, clase));
    }
}
