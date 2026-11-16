package com.miplugin.clases.missions;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class MissionListener implements Listener {

    private final ClasesPlugin plugin;

    /** Minerales colocados por jugadores (en memoria): al romperlos no cuentan para misiones, evita farmear con silk touch. */
    private final Set<String> colocados = new HashSet<>();

    public MissionListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null || killer.getGameMode() != GameMode.SURVIVAL) return;

        plugin.getMissionManager().registrarProgreso(killer, Mission.Tipo.KILL, event.getEntityType().name());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) return;

        Block bloque = event.getBlock();
        if (colocados.remove(clave(bloque))) return;

        plugin.getMissionManager().registrarProgreso(player, Mission.Tipo.MINE, bloque.getType().name());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block bloque = event.getBlockPlaced();
        if (esMineral(bloque.getType())) {
            colocados.add(clave(bloque));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getMissionManager().restaurarBarra(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMissionManager().quitarBarra(event.getPlayer());
        plugin.getMissionManager().guardarSiHaceFalta();
    }

    private boolean esMineral(Material material) {
        String nombre = material.name();
        return nombre.endsWith("_ORE") || nombre.equals("ANCIENT_DEBRIS");
    }

    private String clave(Block bloque) {
        return bloque.getWorld().getUID() + ":" + bloque.getX() + ":" + bloque.getY() + ":" + bloque.getZ();
    }
}
