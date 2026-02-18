package colectivo.negocio;

import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Esta clase implementa el algoritmo de recorrido dijkstra, que calcula el camino entre el origen y el destino,
 * teniendo en cuenta las paradas intermedias, buscando la menor cantidad de paradas.
 */
public class CalculoDijkstra implements AlgoritmoRecorrido {
    /**
     * Calcula el recorrido entre una parada origen y una parada destino, teniendo en cuenta el dia de la semana,
     * la hora de llegada y los tramos disponibles. Este metodo se va a usar en la implementacion del algoritmo de
     * recorrido dijkstra, que busca la menor cantidad de paradas entre el origen y el destino, teniendo en cuenta
     * las paradas intermedias.
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