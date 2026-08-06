package com.miplugin.clases.model;

public class PlayerClaseData {

    private ClaseDefinicion clase;
    private long cooldownExpiraEn;
    private int cambiosRealizados;
    private int cambiosExtra;

    public PlayerClaseData(ClaseDefinicion clase, long cooldownExpiraEn, int cambiosRealizados, int cambiosExtra) {
        this.clase = clase;
        this.cooldownExpiraEn = cooldownExpiraEn;
        this.cambiosRealizados = cambiosRealizados;
        this.cambiosExtra = cambiosExtra;
    }

    public ClaseDefinicion getClase() { return clase; }
    public void setClase(ClaseDefinicion clase) { this.clase = clase; }

    public long getCooldownExpiraEn() { return cooldownExpiraEn; }
    public void setCooldownExpiraEn(long cooldownExpiraEn) { this.cooldownExpiraEn = cooldownExpiraEn; }

    public int getCambiosRealizados() { return cambiosRealizados; }
    public void incrementarCambios() { this.cambiosRealizados++; }

    public int getCambiosExtra() { return cambiosExtra; }
    public void agregarCambioExtra() { this.cambiosExtra++; }

    public boolean puedeElegirOCambiar() {
        return cambiosRealizados < (2 + cambiosExtra);
    }

    public boolean enCooldown() {
        return System.currentTimeMillis() < cooldownExpiraEn;
    }

    public long segundosRestantes() {
        return Math.max(0, (cooldownExpiraEn - System.currentTimeMillis()) / 1000);
    }
}
