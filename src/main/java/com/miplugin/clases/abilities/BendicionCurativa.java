package com.miplugin.clases.abilities;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class BendicionCurativa implements Habilidad {

    private static final double RADIO = 8.0;
    private static final double CURACION = 8.0;

    @Override
    public void ejecutar(Player player) {
        for (Player cercano : player.getWorld().getPlayers()) {
            if (cercano.getLocation().distance(player.getLocation()) > RADIO) continue;

            double maxVida = cercano.getAttribute(Attribute.MAX_HEALTH).getValue();
            cercano.setHealth(Math.min(maxVida, cercano.getHealth() + CURACION));

            cercano.getWorld().spawnParticle(Particle.HEART, cercano.getLocation().add(0, 2, 0), 5, 0.3, 0.3, 0.3, 0);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    @Override
    public String getNombreDisplay() {
        return "Bendición Curativa";
    }
}
