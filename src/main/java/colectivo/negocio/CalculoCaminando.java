package colectivo.negocio;

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


        return soluciones;
    }
}
