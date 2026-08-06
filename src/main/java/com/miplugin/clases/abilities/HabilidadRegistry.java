package com.miplugin.clases.abilities;

import com.miplugin.clases.ClasesPlugin;
import com.miplugin.clases.model.ClaseDefinicion;

import java.util.EnumMap;
import java.util.Map;

public class HabilidadRegistry {

    private final Map<ClaseDefinicion, Habilidad> habilidades = new EnumMap<>(ClaseDefinicion.class);

    public HabilidadRegistry(ClasesPlugin plugin) {
        habilidades.put(ClaseDefinicion.GUERRERO, new GritoDeGuerra());
        habilidades.put(ClaseDefinicion.MAGO, new BolaDeFuego(plugin));
        habilidades.put(ClaseDefinicion.ARQUERO, new LluviaDeFlechas());
        habilidades.put(ClaseDefinicion.TANQUE, new EscudoSagrado());
        habilidades.put(ClaseDefinicion.ASESINO, new PasoSombrio());
        habilidades.put(ClaseDefinicion.CLERIGO, new BendicionCurativa());
    }

    public Habilidad get(ClaseDefinicion clase) {
        return habilidades.get(clase);
    }
}
