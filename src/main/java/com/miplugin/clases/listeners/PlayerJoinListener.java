package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ClasesPlugin plugin;

    public PlayerJoinListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getClaseManager().reaplicarAlEntrar(event.getPlayer());
    }
}
