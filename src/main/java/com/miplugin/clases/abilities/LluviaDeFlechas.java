package com.miplugin.clases.abilities;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LluviaDeFlechas implements Habilidad {

    private static final int[] ANGULOS = {-30, -15, 0, 15, 30};
    private static final double VELOCIDAD = 2.2;

    @Override
    public void ejecutar(Player player) {
        Location origen = player.getEyeLocation();

        for (int angulo : ANGULOS) {
            Vector direccion = origen.getDirection().clone();
            rotarEnY(direccion, angulo);

            Arrow flecha = player.getWorld().spawnArrow(origen, direccion, (float) VELOCIDAD, 0f);
            flecha.setShooter(player);
            flecha.setDamage(flecha.getDamage() * 0.85);
        }
    }

    private void rotarEnY(Vector v, double gradosXY) {
        double rad = Math.toRadians(gradosXY);
        double x = v.getX() * Math.cos(rad) - v.getZ() * Math.sin(rad);
        double z = v.getX() * Math.sin(rad) + v.getZ() * Math.cos(rad);
        v.setX(x).setZ(z);
    }

    @Override
    public String getNombreDisplay() {
        return "Lluvia de Flechas";
    }
}
