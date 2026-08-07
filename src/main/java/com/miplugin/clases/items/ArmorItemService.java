package com.miplugin.clases.items;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ArmorProfile;
import com.miplugin.clases.model.ArmorSlot;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public class ArmorItemService {

    public enum Estado { BASE, FINAL }

    private final ClasesPlugin plugin;
    private final NamespacedKey claseKey;
    private final NamespacedKey slotKey;
    private final NamespacedKey estadoKey;

    public ArmorItemService(ClasesPlugin plugin) {
        this.plugin = plugin;
        this.claseKey = new NamespacedKey(plugin, "armadura_clase");
        this.slotKey = new NamespacedKey(plugin, "armadura_slot");
        this.estadoKey = new NamespacedKey(plugin, "armadura_estado");
    }

    private Material materialVanillaPara(ArmorSlot slot) {
        return switch (slot) {
            case CASCO -> Material.LEATHER_HELMET;
            case PECHERA -> Material.LEATHER_CHESTPLATE;
            case PANTALONES -> Material.LEATHER_LEGGINGS;
            case BOTAS -> Material.LEATHER_BOOTS;
        };
    }

    public ItemStack crear(ClaseDefinicion clase, ArmorSlot slot, Estado estado) {
        ArmorProfile perfil = ArmorProfile.de(clase);
        ItemStack item = new ItemStack(materialVanillaPara(slot));
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(perfil.colorCuero());

        if (estado == Estado.FINAL) {
            meta.setDisplayName("§6" + nombrePieza(clase, slot));
            meta.setLore(java.util.List.of("§7Armadura única de " + clase.getNombreDisplay() + "."));
            int nivel = Math.max(1, Math.min(8, plugin.getConfig().getInt("armor-durability.nivel-irrompibilidad", 3)));
            meta.addEnchant(Enchantment.UNBREAKING, nivel, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS, org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        } else {
            meta.setDisplayName("§7" + nombrePieza(clase, slot) + " (Sin Terminar)");
            meta.setLore(java.util.List.of("§7Llévala a una mesa de herrería", "§7junto a 2 esencias para completarla."));
        }

        meta.getPersistentDataContainer().set(claseKey, PersistentDataType.STRING, clase.name());
        meta.getPersistentDataContainer().set(slotKey, PersistentDataType.STRING, slot.name());
        meta.getPersistentDataContainer().set(estadoKey, PersistentDataType.STRING, estado.name());
        item.setItemMeta(meta);
        return item;
    }

    public String nombrePieza(ClaseDefinicion clase, ArmorSlot slot) {
        String path = "armor-names." + clase.name().toLowerCase() + "." + slot.name().toLowerCase();
        String configurado = plugin.getConfig().getString(path);
        if (configurado != null && !configurado.isBlank()) return configurado;
        return nombreDefault(clase, slot);
    }

    private String nombreDefault(ClaseDefinicion clase, ArmorSlot slot) {
        String pieza = switch (slot) {
            case CASCO -> "Casco";
            case PECHERA -> "Pechera";
            case PANTALONES -> "Pantalones";
            case BOTAS -> "Botas";
        };
        return pieza + " del " + clase.getNombreDisplay();
    }

    public ClaseDefinicion getClase(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(claseKey, PersistentDataType.STRING)) return null;
        try { return ClaseDefinicion.valueOf(pdc.get(claseKey, PersistentDataType.STRING)); }
        catch (IllegalArgumentException e) { return null; }
    }

    public ArmorSlot getSlot(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(slotKey, PersistentDataType.STRING)) return null;
        try { return ArmorSlot.valueOf(pdc.get(slotKey, PersistentDataType.STRING)); }
        catch (IllegalArgumentException e) { return null; }
    }

    public Estado getEstado(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(estadoKey, PersistentDataType.STRING)) return null;
        try { return Estado.valueOf(pdc.get(estadoKey, PersistentDataType.STRING)); }
        catch (IllegalArgumentException e) { return null; }
    }
}
