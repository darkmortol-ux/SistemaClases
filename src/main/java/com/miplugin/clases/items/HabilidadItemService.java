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

import java.util.EnumMap;
import java.util.Map;

public class HabilidadItemService {

    private final ClasesPlugin plugin;
    private final NamespacedKey claseKey;

    private static final Map<ClaseDefinicion, Material> MATERIALES = new EnumMap<>(ClaseDefinicion.class);
    static {
        MATERIALES.put(ClaseDefinicion.GUERRERO, Material.IRON_AXE);
        MATERIALES.put(ClaseDefinicion.MAGO, Material.BLAZE_ROD);
        MATERIALES.put(ClaseDefinicion.ARQUERO, Material.BOW);
        MATERIALES.put(ClaseDefinicion.TANQUE, Material.SHIELD);
        MATERIALES.put(ClaseDefinicion.ASESINO, Material.FEATHER);
        MATERIALES.put(ClaseDefinicion.CLERIGO, Material.STICK);
    }

    public HabilidadItemService(ClasesPlugin plugin) {
        this.plugin = plugin;
        this.claseKey = new NamespacedKey(plugin, "foco_habilidad_clase");
    }

    public ItemStack crear(ClaseDefinicion clase) {
        ItemStack item = new ItemStack(MATERIALES.get(clase));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b" + clase.getNombreDisplay() + " - Foco de Habilidad");
        meta.setLore(java.util.List.of(
                "§7Click derecho para usar",
                "§7tu habilidad activa."
        ));
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(claseKey, PersistentDataType.STRING, clase.name());
        item.setItemMeta(meta);
        return item;
    }

    public boolean esFocoHabilidad(ItemStack item) {
        return getClaseDelFoco(item) != null;
    }

    public ClaseDefinicion getClaseDelFoco(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(claseKey, PersistentDataType.STRING)) return null;

        try {
            return ClaseDefinicion.valueOf(pdc.get(claseKey, PersistentDataType.STRING));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
