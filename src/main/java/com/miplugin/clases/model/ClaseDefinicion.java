package com.miplugin.clases.model;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import java.util.Map;

public enum ClaseDefinicion {

    GUERRERO(
            "Guerrero",
            Map.of(
                    Attribute.MAX_HEALTH, 8.0,
                    Attribute.ATTACK_DAMAGE, 0.15,
                    Attribute.MOVEMENT_SPEED, -0.10
            ),
            25
    ),
    MAGO(
            "Mago",
            Map.of(
                    Attribute.MAX_HEALTH, -4.0,
                    Attribute.ATTACK_SPEED, 0.20
            ),
            12
    ),
    ARQUERO(
            "Arquero",
            Map.of(
                    Attribute.MOVEMENT_SPEED, 0.20,
                    Attribute.ATTACK_SPEED, 0.10
            ),
            20
    ),
    TANQUE(
            "Tanque",
            Map.of(
                    Attribute.MAX_HEALTH, 12.0,
                    Attribute.KNOCKBACK_RESISTANCE, 0.30,
                    Attribute.ATTACK_DAMAGE, -0.15
            ),
            45
    ),
    ASESINO(
            "Asesino",
            Map.of(
                    Attribute.MOVEMENT_SPEED, 0.10
            ),
            25
    ),
    CLERIGO(
            "Clérigo",
            Map.of(
                    Attribute.MAX_HEALTH, 4.0
            ),
            30
    );

    private final String nombreDisplay;
    private final Map<Attribute, Double> modificadores;
    private final int cooldownSegundos;

    ClaseDefinicion(String nombreDisplay, Map<Attribute, Double> modificadores, int cooldownSegundos) {
        this.nombreDisplay = nombreDisplay;
        this.modificadores = modificadores;
        this.cooldownSegundos = cooldownSegundos;
    }

    public String getNombreDisplay() { return nombreDisplay; }
    public Map<Attribute, Double> getModificadores() { return modificadores; }
    public int getCooldownSegundos() { return cooldownSegundos; }

    public AttributeModifier.Operation getOperacionPara(Attribute attribute) {
        if (attribute == Attribute.MAX_HEALTH || attribute == Attribute.KNOCKBACK_RESISTANCE) {
            return AttributeModifier.Operation.ADD_NUMBER;
        }
        return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
    }
}
