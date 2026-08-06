package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class AsesinoCriticoListener implements Listener {

    private static final double BONUS_CRITICO = 0.25;
    private static final double UMBRAL_ESPALDA = -0.5;

    private final ClasesPlugin plugin;

    public AsesinoCriticoListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player atacante)) return;
        if (!(event.getEntity() instanceof LivingEntity victima)) return;

        var data = plugin.getClaseManager().getDatos(atacante.getUniqueId());
        if (data.getClase() != ClaseDefinicion.ASESINO) return;

        boolean bonusAplica = atacante.isSneaking() || esGolpePorLaEspalda(victima, atacante);
        if (!bonusAplica) return;

        event.setDamage(event.getDamage() * (1.0 + BONUS_CRITICO));
    }

    private boolean esGolpePorLaEspalda(LivingEntity victima, Player atacante) {
        Vector direccionVictima = victima.getLocation().getDirection().setY(0).normalize();
        Vector haciaAtacante = atacante.getLocation().toVector()
                .subtract(victima.getLocation().toVector())
                .setY(0);

        if (haciaAtacante.lengthSquared() == 0) return false;
        haciaAtacante.normalize();

        return direccionVictima.dot(haciaAtacante) < UMBRAL_ESPALDA;
    }
}
