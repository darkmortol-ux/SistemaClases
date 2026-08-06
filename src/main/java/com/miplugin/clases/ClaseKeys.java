package com.miplugin.clases;

import org.bukkit.NamespacedKey;

public class ClaseKeys {

    private final NamespacedKey bolaDeFuegoKey;

    public ClaseKeys(ClasesPlugin plugin) {
        this.bolaDeFuegoKey = new NamespacedKey(plugin, "proyectil_bola_de_fuego");
    }

    public NamespacedKey getBolaDeFuegoKey() {
        return bolaDeFuegoKey;
    }
}
