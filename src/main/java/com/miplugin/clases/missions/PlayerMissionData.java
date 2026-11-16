package com.miplugin.clases.missions;

public class PlayerMissionData {

    private Integer misionActiva;   // null = sin misión activa
    private int progreso;
    private long cooldownHasta;     // epoch millis; 0 = sin cooldown
    private int ultimaMision;       // para no repetir la misma dos veces seguidas

    public Integer getMisionActiva() { return misionActiva; }
    public void setMisionActiva(Integer misionActiva) { this.misionActiva = misionActiva; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }

    public long getCooldownHasta() { return cooldownHasta; }
    public void setCooldownHasta(long cooldownHasta) { this.cooldownHasta = cooldownHasta; }

    public int getUltimaMision() { return ultimaMision; }
    public void setUltimaMision(int ultimaMision) { this.ultimaMision = ultimaMision; }

    public boolean enCooldown() {
        return System.currentTimeMillis() < cooldownHasta;
    }

    public long segundosRestantes() {
        return Math.max(0, (cooldownHasta - System.currentTimeMillis()) / 1000);
    }
}
