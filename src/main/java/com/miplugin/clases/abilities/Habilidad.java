package com.miplugin.clases.abilities;

import org.bukkit.entity.Player;

public interface Habilidad {
    void ejecutar(Player player);
    String getNombreDisplay();
}
