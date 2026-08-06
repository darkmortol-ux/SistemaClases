package com.miplugin.clases.abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class GritoDeGuerra implements Habilidad {

    private static final double RADIO = 4.0;
    private static final double FUERZA_EMPUJE = 1.3;

    @Override
    public void ejecutar(Player player) {
        Location origen = player.getLocation();

        for (LivingEntity entidad : player.getWorld().getNearbyLivingEntities(origen, RADIO)) {
            if (entidad.equals(player)) continue;

            Vector direccion = entidad.getLocation().toVector().subtract(origen.toVector());
            if (direccion.lengthSquared() == 0) direccion = new Vector(0, 0, 1);
            direccion.normalize().multiply(FUERZA_EMPUJE).setY(0.35);

            entidad.setVelocity(entidad.getVelocity().add(direccion));
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 1));
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, origen.add(0, 1, 0), 8, 1.5, 0.5, 1.5, 0);
        player.getWorld().playSound(origen, Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.8f);
    }

    @Override
    public String getNombreDisplay() {
        return "Grito de Guerra";
    }
}
