package colectivo.negocio;

import colectivo.aplicacion.Constantes;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Esta clase implementa el algoritmo de recorrido a pie, que calcula el camino a pie entre el origen y el destino,
 * sin tener en cuenta las paradas intermedias, buscando distancia lineal.
 */
public class CalculoCaminando implements AlgoritmoRecorrido {

    /**
     * Calcula el recorrido a pie entre una parada origen y una parada destino, teniendo en cuenta el dia de la semana,
     * la hora de llegada y los tramos disponibles. Este metodo se va a usar en la implementacion del algoritmo de
     * recorrido a pie, que busca la distancia lineal entre el origen y el destino, sin tener en cuenta las paradas
     * intermedias.
     * @param paradaOrigen
     * @param paradaDestino
     * @param diaSemana
     * @param horaLlegada
     * @param tramos
     * @return
     */
    @Override
    public List<List<Recorrido>> calcularRecorrido(Parada paradaOrigen, Parada paradaDestino, int diaSemana,
                                                   LocalTime horaLlegada, Map<String, Tramo> tramos) {
        List<List<Recorrido>> soluciones = new ArrayList<>();

        // Crear un tramo a pie entre la parada de origen y la parada de destino
        Linea lineaPeaton = new Linea();
        lineaPeaton.setCodigo(Constantes.CODIGO_LINEA_PEATON);
        lineaPeaton.setNombre(Constantes.NOMBRE_LINEA_PEATON);

        double distancia = calcularHaversine(paradaOrigen, paradaDestino);
        double tiempoEnMinutos = (distancia / 5.0 * 60);

        List<Recorrido> opcionApie = new ArrayList<>();

        //Creamos el objeto Recorrido para el tramo a pie
        Recorrido tramoCaminando = new Recorrido();
        tramoCaminando.setOrigen(paradaOrigen);
        tramoCaminando.setDestino(paradaDestino);
        tramoCaminando.setLinea(lineaPeaton);
        tramoCaminando.setDuracion((int) Math.round(tiempoEnMinutos));

        opcionApie.add(tramoCaminando);
        soluciones.add(opcionApie);

        return soluciones;
    }

    /**
     * Calcula la distancia entre dos paradas utilizando la formula de Haversine, que es una formula que se utiliza
     * para calcular la distancia entre dos puntos en la superficie de una esfera a partir de sus latitudes y
     * longitudes.
     * @param p1 punto 1, representado como un objeto Parada, que contiene la latitud y longitud del punto 1 de origen
     * @param p2 punto 2, representado como un objeto Parada, que contiene la latitud y longitud del punto 2 de destino
     * @return
     */
    private double calcularHaversine(Parada p1, Parada p2) {
        double radioTierra = 6371; // Radio de la Tierra en kilómetros
        double dlat = Math.toRadians(p2.getLatitud() - p1.getLatitud());
        double dlon = Math.toRadians(p2.getLongitud() - p1.getLongitud());

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(Math.toRadians(p1.getLatitud())) * Math.cos(Math.toRadians(p2.getLatitud())) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioTierra * c;
    }
}