package colectivo.aplicacion;

import java.time.LocalTime;

public class Tiempo {

    /**
     * Convierte los segundos totales de un recorrido a "hh:mm" (ej. 01:45)
     */
    public static String formatearTiempoTotal(int totalSegundos) {
        int minutosTotales = totalSegundos / 60;
        int horas = minutosTotales / 60;
        int minutos = minutosTotales % 60;
        return String.format("%02d:%02d", horas, minutos);
    }

    /**
     * Convierte los segundos de un tramo a "mm:ss" (ej. 03:20)
     */
    public static String formatearDuracionTramo(int segundos) {
        // 1. Primero convertimos todo a minutos totales
        int minutosTotales = segundos / 60;

        // 2. Separamos esos minutos en Horas y Minutos
        int horas = minutosTotales / 60;
        int minutos = minutosTotales % 60;

        // 3. Devolvemos el string con formato HH:mm
        return String.format("%02d:%02d", horas, minutos);
    }

    /**
     * Suma segundos a un reloj base y devuelve la nueva hora
     */
    public static LocalTime sumarSegundos(LocalTime horaBase, int segundos) {
        return horaBase.plusSeconds(segundos);
    }
}
