package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Set;

public class ArmorSetListener implements Listener {

    private static final Set<EquipmentSlot> SLOTS_ARMADURA = Set.of(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    );

    private final ClasesPlugin plugin;

    public ArmorSetListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEquipmentChanged(EntityEquipmentChangedEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Solo nos interesa si cambió algún slot de armadura (casco/pechera/pantalones/botas),
        // ignoramos cambios de mano principal/offhand.
        boolean cambioArmadura = event.getEquipmentChanges().keySet().stream()
                .anyMatch(SLOTS_ARMADURA::contains);
        if (!cambioArmadura) return;

        var clase = plugin.getClaseManager().getDatos(player.getUniqueId()).getClase();
        plugin.getArmorMitigationService().actualizar(player, clase);
    }
}
