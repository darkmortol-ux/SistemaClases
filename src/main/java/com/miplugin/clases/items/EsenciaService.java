package com.miplugin.clases.items;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ArmorProfile;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class EsenciaService {

    private final ClasesPlugin plugin;
    private final NamespacedKey key;
    private final Random random = new Random();

    public EsenciaService(ClasesPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "esencia_de_clase");
    }

    public ItemStack crear(ClaseDefinicion clase) {
        ArmorProfile perfil = ArmorProfile.de(clase);
        ItemStack item = new ItemStack(perfil.materialEsencia());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d" + perfil.nombreEsencia());
        meta.setLore(java.util.List.of(
                "§7Esencia de " + clase.getNombreDisplay() + ".",
                "§7Se usa para craftear la armadura",
                "§7única de esta clase."
        ));
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, clase.name());
        item.setItemMeta(meta);
        return item;
    }

    /** Crea una esencia de una clase aleatoria entre las 6. Usada por el drop del jefe. */
    public ItemStack crearAleatoria() {
        ClaseDefinicion[] clases = ClaseDefinicion.values();
        ClaseDefinicion elegida = clases[random.nextInt(clases.length)];
        return crear(elegida);
    }

    public ClaseDefinicion getClaseDeEsencia(ItemStack item) {
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
