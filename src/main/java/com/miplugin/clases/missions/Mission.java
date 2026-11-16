package com.miplugin.clases.missions;

/** Una misión del aldeano. Se define en misiones.yml. */
public record Mission(int id, Mission.Dificultad dificultad, Mission.Tipo tipo, String objetivo, int cantidad) {

    public enum Tipo { KILL, MINE, ENTREGAR }

    public enum Dificultad { DIFICIL, EXTREMA }

    public String descripcion() {
        String nombre = bonito(objetivo);
        return switch (tipo) {
            case KILL -> "Elimina " + cantidad + " " + nombre;
            case MINE -> "Mina " + cantidad + " de " + nombre;
            case ENTREGAR -> "Entrega " + cantidad + "x " + nombre;
        };
    }

    /** ZOMBIFIED_PIGLIN -> Zombified Piglin */
    public static String bonito(String enumName) {
        StringBuilder sb = new StringBuilder();
        for (String parte : enumName.toLowerCase().split("_")) {
            if (parte.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(parte.charAt(0))).append(parte.substring(1));
        }
        return sb.toString();
    }
}
