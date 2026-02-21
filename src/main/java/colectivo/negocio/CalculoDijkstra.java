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

    /**
     * Clase interna que representa un nodo en el algoritmo de Dijkstra, con una parada y el tiempo acumulado para
     * llegar a esa parada.
     * Se declaro inner class por ser una clase que solo se va a usar dentro de la clase CalculoDijkstra,
     * y para mantener el codigo organizado y encapsulado.
     * Es estatica porque no necesita acceder a los miembros de la clase externa, y se puede instanciar sin necesidad
     * de una instancia de la clase externa.
     */
    private static class NodoDijkstra {

        /**
         * Parada representada por el nodo de Dijkstra
         */
        private final Parada parada;

        /**
         * Tiempo acumulado para llegar a la parada representada por el nodo de Dijkstra
         */
        private final int tiempoAcumulado;

        /**
         * Constructor de la clase NodoDijkstra con la parada y el tiempo acumulado para llegar a esa parada
         * @param parada
         * @param tiempoAcumulado
         */
        public NodoDijkstra(Parada parada, int tiempoAcumulado) {
            this.parada = parada;
            this.tiempoAcumulado = tiempoAcumulado;
        }

        /**
         * Obtiene la parada representada por el nodo de Dijkstra
         * @return
         */
        public Parada getParada() {
            return parada;
        }

        /**
         * Obtiene el tiempo acumulado para llegar a la parada representada por el nodo de Dijkstra
         * @return
         */
        public int getTiempoAcumulado() {
            return tiempoAcumulado;
        }

        /**
         * Obtiene una representación en forma de cadena del nodo de Dijkstra, mostrando la parada
         * y el tiempo acumulado
         * @return
         */
        @Override
        public String toString() {
            return "NodoDijkstra{" +
                    "parada=" + parada.getCodigo() +
                    ", tiempoAcumulado=" + tiempoAcumulado +
                    '}';
        }
    }
}