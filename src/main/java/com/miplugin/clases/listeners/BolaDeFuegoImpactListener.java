package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

public class BolaDeFuegoImpactListener implements Listener {

    private static final double RADIO_EXPLOSION = 3.0;
    private static final double DANO = 6.0;

    private final ClasesPlugin plugin;

    public BolaDeFuegoImpactListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onImpacto(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!snowball.getPersistentDataContainer().has(plugin.getKeys().getBolaDeFuegoKey(), PersistentDataType.BYTE)) return;

        Location punto = snowball.getLocation();

        for (LivingEntity entidad : punto.getWorld().getNearbyLivingEntities(punto, RADIO_EXPLOSION)) {
            if (snowball.getShooter() == entidad) continue;
            entidad.damage(DANO, snowball.getShooter() instanceof org.bukkit.entity.Entity e ? e : null);
        }

        punto.getWorld().spawnParticle(Particle.EXPLOSION, punto, 1);
        punto.getWorld().playSound(punto, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
    }
}
