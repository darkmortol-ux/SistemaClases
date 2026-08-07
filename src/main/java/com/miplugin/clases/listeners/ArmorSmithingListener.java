package com.miplugin.clases.listeners;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.items.ArmorItemService;
import com.miplugin.clases.model.ArmorSlot;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

public class ArmorSmithingListener implements Listener {

    private static final int SLOT_BASE = 1;
    private static final int SLOT_MATERIAL = 2;

    private final ClasesPlugin plugin;

    public ArmorSmithingListener(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        ItemStack base = inv.getItem(SLOT_BASE);
        ItemStack material = inv.getItem(SLOT_MATERIAL);

        ItemStack resultado = calcularResultado(base, material);
        event.setResult(resultado);
    }

    @EventHandler
    public void onSmith(SmithItemEvent event) {
        if (!(event.getWhoClicked() instanceof HumanEntity)) return;

        SmithingInventory inv = event.getInventory();
        ItemStack base = inv.getItem(SLOT_BASE);
        ItemStack material = inv.getItem(SLOT_MATERIAL);

        ItemStack resultadoEsperado = calcularResultado(base, material);
        if (resultadoEsperado == null) {
            event.setCancelled(true);
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ItemStack materialRestante = inv.getItem(SLOT_MATERIAL);
            if (materialRestante != null && materialRestante.getAmount() > 0) {
                materialRestante.setAmount(materialRestante.getAmount() - 1);
                inv.setItem(SLOT_MATERIAL, materialRestante.getAmount() <= 0 ? null : materialRestante);
            }
        });
    }

    private ItemStack calcularResultado(ItemStack base, ItemStack material) {
        if (base == null || material == null) return null;

        ArmorItemService armorService = plugin.getArmorItemService();
        ClaseDefinicion claseBase = armorService.getClase(base);
        ArmorSlot slot = armorService.getSlot(base);
        ArmorItemService.Estado estado = armorService.getEstado(base);

        if (claseBase == null || estado != ArmorItemService.Estado.BASE) return null;
        if (slot != ArmorSlot.PECHERA && slot != ArmorSlot.PANTALONES) return null;

        ClaseDefinicion claseEsencia = plugin.getEsenciaService().getClaseDeEsencia(material);
        if (claseEsencia != claseBase) return null;
        if (material.getAmount() < 2) return null;

        return armorService.crear(claseBase, slot, ArmorItemService.Estado.FINAL);
    }
}
