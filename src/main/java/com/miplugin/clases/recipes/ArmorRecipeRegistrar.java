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
}
