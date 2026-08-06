package com.miplugin.clases.abilities;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.persistence.PersistentDataType;

public class BolaDeFuego implements Habilidad {

    private final ClasesPlugin plugin;

    public BolaDeFuego(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void ejecutar(Player player) {
        Snowball proyectil = player.launchProjectile(Snowball.class);
        proyectil.setVelocity(player.getLocation().getDirection().multiply(1.4));
        proyectil.getPersistentDataContainer().set(
                plugin.getKeys().getBolaDeFuegoKey(), PersistentDataType.BYTE, (byte) 1
        );
    }

    @Override
    public String getNombreDisplay() {
        return "Bola de Fuego";
    }
}
