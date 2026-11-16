package com.miplugin.clases.recipes;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.items.ArmorItemService;
import com.miplugin.clases.model.ArmorProfile;
import com.miplugin.clases.model.ArmorSlot;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;

public class ArmorRecipeRegistrar {

    private final ClasesPlugin plugin;

    public ArmorRecipeRegistrar(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    public void registrarTodas() {
        for (ClaseDefinicion clase : ClaseDefinicion.values()) {
            registrarCasco(clase);
            registrarBotas(clase);
            registrarPechera(clase);
            registrarPantalones(clase);
            registrarLingote(clase);
            registrarHerreria(clase, ArmorSlot.PECHERA);
            registrarHerreria(clase, ArmorSlot.PANTALONES);
        }
    }

    private RecipeChoice.MaterialChoice cuero() {
        return new RecipeChoice.MaterialChoice(Material.LEATHER);
    }

    private RecipeChoice.MaterialChoice relleno(ClaseDefinicion clase) {
        return new RecipeChoice.MaterialChoice(ArmorProfile.de(clase).materialRelleno());
    }

    private RecipeChoice.ExactChoice esencia(ClaseDefinicion clase) {
        return new RecipeChoice.ExactChoice(plugin.getEsenciaService().crear(clase));
    }

    private void registrarCasco(ClaseDefinicion clase) {
        var item = plugin.getArmorItemService().crear(clase, ArmorSlot.CASCO, ArmorItemService.Estado.FINAL);
        var key = new NamespacedKey(plugin, "casco_" + clase.name().toLowerCase());
        ShapedRecipe receta = new ShapedRecipe(key, item);
        receta.shape("LLL", "RER", "L L");
        receta.setIngredient('L', cuero());
        receta.setIngredient('R', relleno(clase));
        receta.setIngredient('E', esencia(clase));
        plugin.getServer().addRecipe(receta);
    }

    private void registrarBotas(ClaseDefinicion clase) {
        var item = plugin.getArmorItemService().crear(clase, ArmorSlot.BOTAS, ArmorItemService.Estado.FINAL);
        var key = new NamespacedKey(plugin, "botas_" + clase.name().toLowerCase());
        ShapedRecipe receta = new ShapedRecipe(key, item);
        receta.shape("L L", "RER", "L L");
        receta.setIngredient('L', cuero());
        receta.setIngredient('R', relleno(clase));
        receta.setIngredient('E', esencia(clase));
        plugin.getServer().addRecipe(receta);
    }

    private void registrarPechera(ClaseDefinicion clase) {
        var item = plugin.getArmorItemService().crear(clase, ArmorSlot.PECHERA, ArmorItemService.Estado.BASE);
        var key = new NamespacedKey(plugin, "pechera_base_" + clase.name().toLowerCase());
        ShapedRecipe receta = new ShapedRecipe(key, item);
        receta.shape("LLL", "RLR", "L L");
        receta.setIngredient('L', cuero());
        receta.setIngredient('R', relleno(clase));
        plugin.getServer().addRecipe(receta);
    }

    private void registrarPantalones(ClaseDefinicion clase) {
        var item = plugin.getArmorItemService().crear(clase, ArmorSlot.PANTALONES, ArmorItemService.Estado.BASE);
        var key = new NamespacedKey(plugin, "pantalones_base_" + clase.name().toLowerCase());
        ShapedRecipe receta = new ShapedRecipe(key, item);
        receta.shape("LRL", "LLL", " R ");
        receta.setIngredient('L', cuero());
        receta.setIngredient('R', relleno(clase));
        plugin.getServer().addRecipe(receta);
    }

    /** Lingote especial: 1 lingote de netherite al centro, rodeado por 8 esencias de la clase. */
    private void registrarLingote(ClaseDefinicion clase) {
        var key = new NamespacedKey(plugin, "lingote_" + clase.name().toLowerCase());
        ShapedRecipe receta = new ShapedRecipe(key, plugin.getLingoteService().crear(clase));
        receta.shape("EEE", "ENE", "EEE");
        receta.setIngredient('E', esencia(clase));
        receta.setIngredient('N', new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
        plugin.getServer().addRecipe(receta);
    }

    /** Mesa de herrería: pieza sin terminar + lingote especial de la misma clase = pieza final. Sin plantilla. */
    private void registrarHerreria(ClaseDefinicion clase, ArmorSlot slot) {
        var key = new NamespacedKey(plugin, slot.name().toLowerCase() + "_herreria_" + clase.name().toLowerCase());
        var resultado = plugin.getArmorItemService().crear(clase, slot, ArmorItemService.Estado.FINAL);
        var base = new RecipeChoice.ExactChoice(
                plugin.getArmorItemService().crear(clase, slot, ArmorItemService.Estado.BASE));
        var adicion = new RecipeChoice.ExactChoice(plugin.getLingoteService().crear(clase));

        SmithingTransformRecipe receta = new SmithingTransformRecipe(
                key, resultado, RecipeChoice.empty(), base, adicion, false);
        plugin.getServer().addRecipe(receta);
    }
}
