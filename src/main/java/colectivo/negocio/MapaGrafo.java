package colectivo.negocio;

import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase representa a la estructura de datos antes de calcular, convierte los Map planos en una red donde las
 * paradas conoces a sus vecinos parada. Transforma los mapas del servicio en uan lista de adyacencia. Esta es clave
 * para que "Dijkstra" funcione, ya que necesita conocer las conexiones entre las paradas para calcular el camino más
 * corto.
 */
public class MapaGrafo {

    /**
     * Mapa de adyacencias, donde la clave es el identificador de la parada y el valor es una lista de tramos que
     * conectan esa parada con otras paradas.
     */
    private Map<Integer, List<Tramo>> adyacencias;

    /**
     * Constructor de la clase MapaGrafo, inicializa el mapa de adyacencias como un HashMap vacío.
     */
    public MapaGrafo() {
        this.adyacencias = new HashMap<>();
    }

    /**
     * Construye el grafo a partir de paradas y tramos. para cada parada, se inicializa una lista vacia en el mapa de
     * adyacencias tomando el codigo de la parada como clave. Luego, se recorren los tramos y se agregan a la lista de
     * adyacencias de la parada de inicio del tramo, creando asi las conexiones entre las paradas.
     * @param paradas mapa de paradas disponibles, donde la clave es el identificador de la parada
     *               y el valor es el objeto Parada
     * @param tramos mapa de tramos disponibles, donde la clave es el identificador del tramo y el valor es el
     *              objeto Tramo
     */
    public void construirGrafo(Map<Integer, Parada> paradas, Map<String, Tramo> tramos) {

        //Inicializar el mapa de adyacencias con las paradas
        for (Integer id : paradas.keySet()) {
            adyacencias.put(id, new ArrayList<>());
        }

        //Llenar el mapa de adyacencias con los tramos
        for (Tramo tramo : tramos.values()) {

            //Esto es para que Dijkstra muestre recorridos en colectivo y no A PIE, es mas que nada para practica
            if (tramo.getTipo() == 1) {
                if (adyacencias.containsKey(tramo.getInicio().getCodigo())) {
                    adyacencias.get(tramo.getInicio().getCodigo()).add(tramo);
                }
            }

            //
            //if (adyacencias.containsKey(tramo.getInicio().getCodigo())) {
              //  adyacencias.get(tramo.getInicio().getCodigo()).add(tramo);
            //}
        }
    }

    /**
     * Obtiene la lista de tramos que conectan una parada dada con otras paradas. Si la parada no tiene tramos asociados,
     * se devuelve una lista vacía.
     * @param idParada identificador de la parada para la cual se desean obtener los tramos
     * @return lista de tramos que conectan la parada dada con otras paradas, o una lista vacía si no hay tramos
     * asociados a esa parada.
     */
    public List<Tramo> obtenerTramosDesde(int idParada) {
        return adyacencias.getOrDefault(idParada, new ArrayList<>());
    }
}
