package com.miplugin.clases.items;

import com.miplugin.clases.ClasesPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EstrellaCambioService {

    private final ClasesPlugin plugin;
    private final NamespacedKey key;

    public EstrellaCambioService(ClasesPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "estrella_del_cambio");
    }

    public ItemStack crear() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d★ Estrella del Cambio");

        int precio = plugin.getConfig().getInt("precio-estrella-cambio", 500000);
        meta.setLore(java.util.List.of(
                "§7Permite cambiar de clase una vez más,",
                "§7incluso si ya usaste tu límite.",
                "§7",
                "§eValor de referencia: §f$" + String.format("%,d", precio),
                "§cSe consume al usarse."
        ));

        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean esEstrellaCambio(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    public int buscarEnHotbar(Player player) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (esEstrellaCambio(inv.getItem(i))) {
                return i;
            }
        }
        return -1;
    }

    public void consumir(Player player, int slot) {
        var inv = player.getInventory();
        var item = inv.getItem(slot);
        if (item == null) return;

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            inv.setItem(slot, null);
        }
    }
}
