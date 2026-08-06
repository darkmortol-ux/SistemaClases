package com.miplugin.clases.abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class PasoSombrio implements Habilidad {

    private static final double DISTANCIA = 6.0;

    @Override
    public void ejecutar(Player player) {
        Location origen = player.getEyeLocation();
        Vector direccion = origen.getDirection();

        RayTraceResult resultado = player.getWorld().rayTraceBlocks(origen, direccion, DISTANCIA);
        double distanciaReal = (resultado != null)
                ? origen.distance(resultado.getHitPosition().toLocation(player.getWorld())) - 0.5
                : DISTANCIA;

        Location destino = origen.clone().add(direccion.clone().multiply(Math.max(1.0, distanciaReal)));
        destino.setY(player.getLocation().getY());
        destino.setDirection(player.getLocation().getDirection());

        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 20, 0.3, 0.5, 0.3, 0.02);
        player.teleport(destino);
        player.getWorld().spawnParticle(Particle.SMOKE, destino, 20, 0.3, 0.5, 0.3, 0.02);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1));
        player.getWorld().playSound(destino, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
    }

    @Override
    public String getNombreDisplay() {
        return "Paso Sombrío";
    }
}
