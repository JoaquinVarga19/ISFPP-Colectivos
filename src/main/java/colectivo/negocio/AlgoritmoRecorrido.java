package colectivo.negocio;

import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Interfaz que usa el calculo dado por la catedra en distintos contextos de recorrido, como el algoritmo directo,
 * caminando o dijkstra.
 * Directo: se calcula el camino directo entre el origen y el destino, sin tener en cuenta las paradas intermedias, 
 * buscando una sola linea.
 * Caminando: se calcula el camino a pie entre el origen y el destino, sin tener en cuenta las paradas intermedias,
 * buscando distancia lineal.
 * Dijkstra: se calcula el camino entre el origen y el destino, teniendo en cuenta las paradas intermedias, buscando
 * la menor cantidad de paradas.
 */
public interface AlgoritmoRecorrido {

    /**
     * Calcula el recorrido entre una parada origen y una parada destino, teniendo en cuenta el dia de la semana,
     * la hora de llegada y los tramos disponibles.
     * Este metodo se va a usar en las distintas implementaciones de los algoritmos de recorrido, como el algoritmo
     * directo, caminando o dijkstra.
     * @param paradaOrigen parada de origen del recorrido
     * @param paradaDestino parada de destino del recorrido
     * @param diaSemana dia de la semana en el que se va a realizar el recorrido, representado como un entero del
     *                 1 al 7, donde 1 es lunes y 7 es domingo/feriado
     * @param horaLlegada hora de llegada al destino, representada como un objeto LocalTime
     * @param tramos mapa de tramos disponibles, donde la clave es el identificador del tramo y el valor es el objeto
     *              Tramo
     * @return
     */
    List<List<Recorrido>> calcularRecorrido(Parada paradaOrigen, Parada paradaDestino, int diaSemana,
                                            LocalTime horaLlegada,
                                            Map<String, Tramo> tramos);
}
