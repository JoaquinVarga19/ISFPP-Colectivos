package colectivo.negocio;

import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Esta clase implementa el algoritmo de recorrido directo, que calcula el camino directo entre el origen y el destino,
 * sin tener en cuenta las paradas intermedias, buscando una sola linea.
 */
public class CalculoDirecto implements AlgoritmoRecorrido {

    /**
     * Calcula el recorrido directo entre una parada origen y una parada destino, teniendo en cuenta el dia de la
     * semana, la hora de llegada y los tramos disponibles. Este metodo se va a usar en la implementacion del algoritmo
     * de recorrido directo, que busca una sola linea entre el origen y el destino, sin tener en cuenta las paradas
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
