package com.miplugin.clases.missions;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public class AldeanoService {

    private final NamespacedKey key;

    public AldeanoService(ClasesPlugin plugin) {
        this.key = new NamespacedKey(plugin, "aldeano_misiones");
    }

    /** Crea el aldeano de misiones: quieto, invulnerable, sin sonidos y que no desaparece. */
    public Villager crear(Location ubicacion) {
        return ubicacion.getWorld().spawn(ubicacion, Villager.class, villager -> {
            villager.setProfession(Villager.Profession.CLERIC);
            villager.setAI(false);
            villager.setInvulnerable(true);
            villager.setSilent(true);
            villager.setPersistent(true);
            villager.setRemoveWhenFarAway(false);
            villager.setCustomName("§d✦ Aldeano de las Esencias");
            villager.setCustomNameVisible(true);
            villager.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        });
    }

    public boolean esAldeano(Entity entidad) {
        return entidad instanceof Villager
                && entidad.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    /** Quita el aldeano de misiones más cercano dentro del radio. Devuelve false si no hay ninguno. */
    public boolean quitarCercano(Location origen, double radio) {
        Entity masCercano = null;
        double mejorDistancia = Double.MAX_VALUE;

        for (Entity entidad : origen.getWorld().getNearbyEntities(origen, radio, radio, radio)) {
            if (!esAldeano(entidad)) continue;
            double distancia = entidad.getLocation().distanceSquared(origen);
            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                masCercano = entidad;
            }
        }

        if (masCercano == null) return false;
        masCercano.remove();
        return true;
    }
}
