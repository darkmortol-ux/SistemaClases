package com.miplugin.clases.items;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class LingoteService {

    private final NamespacedKey key;

    public LingoteService(ClasesPlugin plugin) {
        this.key = new NamespacedKey(plugin, "lingote_especial_clase");
    }

    /** Lingote especial de una clase: se craftea con 1 lingote de netherite + 8 esencias de esa clase. */
    public ItemStack crear(ClaseDefinicion clase) {
        ItemStack item = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Lingote Especial del " + clase.getNombreDisplay());
        meta.setLore(java.util.List.of(
                "§7Colócalo en una mesa de herrería",
                "§7junto a una pieza sin terminar",
                "§7(pechera o pantalones) de " + clase.getNombreDisplay() + "."
        ));
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, clase.name());
        item.setItemMeta(meta);
        return item;
    }

    public ClaseDefinicion getClase(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(key, PersistentDataType.STRING)) return null;
        try {
            return ClaseDefinicion.valueOf(pdc.get(key, PersistentDataType.STRING));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
