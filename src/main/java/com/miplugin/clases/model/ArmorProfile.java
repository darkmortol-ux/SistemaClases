package com.miplugin.clases.model;

import org.bukkit.Color;
import org.bukkit.Material;

public record ArmorProfile(
        Color colorCuero,
        Material materialRelleno,
        Material materialEsencia,
        String nombreEsencia
) {
    public static ArmorProfile de(ClaseDefinicion clase) {
        return switch (clase) {
            case GUERRERO -> new ArmorProfile(Color.fromRGB(139, 0, 0), Material.BLAZE_POWDER, Material.MAGMA_CREAM, "Corazón de Furia");
            case MAGO -> new ArmorProfile(Color.fromRGB(0, 0, 205), Material.AMETHYST_SHARD, Material.PRISMARINE_CRYSTALS, "Cristal Arcano");
            case TANQUE -> new ArmorProfile(Color.fromRGB(192, 192, 192), Material.IRON_BLOCK, Material.ECHO_SHARD, "Núcleo Blindado");
            case ARQUERO -> new ArmorProfile(Color.fromRGB(34, 139, 34), Material.EMERALD, Material.SPECTRAL_ARROW, "Pluma Certera");
            case ASESINO -> new ArmorProfile(Color.fromRGB(75, 0, 130), Material.ENDER_PEARL, Material.PHANTOM_MEMBRANE, "Sombra Condensada");
            case CLERIGO -> new ArmorProfile(Color.fromRGB(255, 215, 0), Material.GHAST_TEAR, Material.NAUTILUS_SHELL, "Lágrima Sagrada");
        };
    }
}
