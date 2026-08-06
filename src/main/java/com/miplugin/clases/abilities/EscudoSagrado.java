package com.miplugin.clases.abilities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EscudoSagrado implements Habilidad {

    private static final long DURACION_MILIS = 5500;
    private static final double REDUCCION = 0.5;

    private final Map<UUID, Long> escudosActivos = new HashMap<>();

    @Override
    public void ejecutar(Player player) {
        escudosActivos.put(player.getUniqueId(), System.currentTimeMillis() + DURACION_MILIS);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (int) (DURACION_MILIS / 50), 0));
    }

    public boolean tieneEscudoActivo(UUID uuid) {
        Long expira = escudosActivos.get(uuid);
        return expira != null && System.currentTimeMillis() < expira;
    }

    public double getReduccion() {
        return REDUCCION;
    }

    @Override
    public String getNombreDisplay() {
        return "Escudo Sagrado";
    }
}
