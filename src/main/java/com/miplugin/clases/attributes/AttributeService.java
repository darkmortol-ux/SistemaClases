package com.miplugin.clases.attributes;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.Map;

public class AttributeService {

    private final ClasesPlugin plugin;

    public AttributeService(ClasesPlugin plugin) {
        this.plugin = plugin;
    }

    public void limpiarModificadores(Player player) {
        for (ClaseDefinicion clase : ClaseDefinicion.values()) {
            for (Attribute attribute : clase.getModificadores().keySet()) {
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

    public void aplicarModificadores(Player player, ClaseDefinicion clase) {
        for (Map.Entry<Attribute, Double> entry : clase.getModificadores().entrySet()) {
            Attribute attribute = entry.getKey();
            double valor = entry.getValue();

            AttributeInstance instancia = player.getAttribute(attribute);
            if (instancia == null) {
                plugin.getLogger().warning("El jugador " + player.getName()
                        + " no tiene el atributo " + attribute + " (¿versión de servidor incorrecta?)");
                continue;
            }

            NamespacedKey key = keyPara(clase, attribute);
            AttributeModifier modifier = new AttributeModifier(
                    key,
                    valor,
                    clase.getOperacionPara(attribute)
            );
            instancia.addModifier(modifier);
        }

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null && player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }
    }

    public void cambiarClase(Player player, ClaseDefinicion nuevaClase) {
        limpiarModificadores(player);
        aplicarModificadores(player, nuevaClase);
    }

    private NamespacedKey keyPara(ClaseDefinicion clase, Attribute attribute) {
        String claveTexto = "clase_" + clase.name().toLowerCase() + "_" + attribute.getKey().getKey();
        return new NamespacedKey(plugin, claveTexto);
    }
}
