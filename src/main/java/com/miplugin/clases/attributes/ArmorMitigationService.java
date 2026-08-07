package com.miplugin.clases.attributes;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

public class ArmorMitigationService {

    private final ClasesPlugin plugin;

    private static final Map<ClaseDefinicion, Map<Attribute, Double>> COMPENSACIONES = new EnumMap<>(ClaseDefinicion.class);
    static {
        COMPENSACIONES.put(ClaseDefinicion.GUERRERO, Map.of(Attribute.MOVEMENT_SPEED, 0.05));
        COMPENSACIONES.put(ClaseDefinicion.MAGO, Map.of(Attribute.MAX_HEALTH, 2.0));
        COMPENSACIONES.put(ClaseDefinicion.TANQUE, Map.of(Attribute.ATTACK_DAMAGE, 0.075));
        COMPENSACIONES.put(ClaseDefinicion.ARQUERO, Map.of(
                Attribute.MAX_HEALTH, 2.0, Attribute.KNOCKBACK_RESISTANCE, 0.05));
        COMPENSACIONES.put(ClaseDefinicion.ASESINO, Map.of(
                Attribute.MAX_HEALTH, 2.0, Attribute.KNOCKBACK_RESISTANCE, 0.05));
        COMPENSACIONES.put(ClaseDefinicion.CLERIGO, Map.of(
                Attribute.MAX_HEALTH, 2.0, Attribute.KNOCKBACK_RESISTANCE, 0.05));
    }

    public ArmorMitigationService(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    public void quitarTodas(Player player) {
        for (ClaseDefinicion clase : ClaseDefinicion.values()) {
            Map<Attribute, Double> comp = COMPENSACIONES.get(clase);
            if (comp == null) continue;
            for (Attribute attribute : comp.keySet()) {
                AttributeInstance instancia = player.getAttribute(attribute);
                if (instancia == null) continue;
                NamespacedKey key = keyPara(clase, attribute);
                instancia.getModifiers().stream()
                        .filter(m -> m.getKey().equals(key))
                        .toList()
                        .forEach(instancia::removeModifier);
            }
        }
    }

    public void aplicar(Player player, ClaseDefinicion clase) {
        Map<Attribute, Double> comp = COMPENSACIONES.get(clase);
        if (comp == null) return;

        for (Map.Entry<Attribute, Double> entry : comp.entrySet()) {
            AttributeInstance instancia = player.getAttribute(entry.getKey());
            if (instancia == null) continue;

            NamespacedKey key = keyPara(clase, entry.getKey());
            AttributeModifier.Operation operacion = clase.getOperacionPara(entry.getKey());
            instancia.addModifier(new AttributeModifier(key, entry.getValue(), operacion));
        }
    }

    public void actualizar(Player player, ClaseDefinicion claseDelJugador) {
        quitarTodas(player);

        if (claseDelJugador == null) return;
        if (!tieneSetCompletoDe(player, claseDelJugador)) return;

        aplicar(player, claseDelJugador);
    }

    private boolean tieneSetCompletoDe(Player player, ClaseDefinicion clase) {
        var armorService = plugin.getArmorItemService();
        var equipo = player.getInventory();

        return coincide(equipo.getHelmet(), clase, armorService)
                && coincide(equipo.getChestplate(), clase, armorService)
                && coincide(equipo.getLeggings(), clase, armorService)
                && coincide(equipo.getBoots(), clase, armorService);
    }

    private boolean coincide(org.bukkit.inventory.ItemStack pieza, ClaseDefinicion clase,
                              com.miplugin.clases.items.ArmorItemService armorService) {
        if (pieza == null) return false;
        if (armorService.getEstado(pieza) != com.miplugin.clases.items.ArmorItemService.Estado.FINAL) return false;
        return armorService.getClase(pieza) == clase;
    }

    private NamespacedKey keyPara(ClaseDefinicion clase, Attribute attribute) {
        return new NamespacedKey(plugin, "armadura_mitigacion_" + clase.name().toLowerCase() + "_" + attribute.getKey().getKey());
    }
}
