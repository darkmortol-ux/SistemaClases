package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.abilities.EscudoSagrado;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class TanqueEscudoListener implements Listener {

    private final ClasesPlugin plugin;

    public TanqueEscudoListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victima)) return;

        var data = plugin.getClaseManager().getDatos(victima.getUniqueId());
        if (data.getClase() != ClaseDefinicion.TANQUE) return;

        EscudoSagrado escudo = (EscudoSagrado) plugin.getHabilidadRegistry().get(ClaseDefinicion.TANQUE);
        if (!escudo.tieneEscudoActivo(victima.getUniqueId())) return;

        event.setDamage(event.getDamage() * (1.0 - escudo.getReduccion()));
    }
}
