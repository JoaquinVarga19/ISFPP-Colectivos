package colectivo.negocio;

import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.*;
import java.time.temporal.ChronoUnit;
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

        //Objeto MapaGrafo
        MapaGrafo mapaGrafo = new MapaGrafo();

        //Extraemos las paradas de los tramos disponibles y los almacenamos en un mapa donde la clave es el codigo
        //y el valor el objeto tipo parada
        Map<Integer, Parada> todasLasParadas = extraerParadaTramo(tramos);

        //Construimos el grafo a partir de las paradas y los tramos disponibles, utilizando el objeto MapaGrafo
        mapaGrafo.construirGrafo(todasLasParadas, tramos);

        //guardamos el tiempo minimo para llegar a cada parada
        Map<Integer, Integer> tiemposMinimos = new HashMap<>();

        //guardamos el tramo padre para cada parada, es decir, el tramo que se utilizo para llegar a esa parada
        //con el tiempo minimo
        Map<Integer, Tramo> tramosPadres = new HashMap<>();

        //inicialimos el tiempo minimo para llegar a cada parada como infinito
        for (Integer idParada : todasLasParadas.keySet()) {
            tiemposMinimos.put(idParada, Integer.MAX_VALUE);
        }

        //configuracion de los puntos de partida
        tiemposMinimos.put(paradaOrigen.getCodigo(), 0);
        PriorityQueue<NodoDijkstra> cola = new PriorityQueue<>(Comparator.comparingInt(NodoDijkstra::getTiempoAcumulado));
        cola.add(new NodoDijkstra(paradaOrigen, 0));

        //inicializacion bucle while---
        while (!cola.isEmpty()) {
            NodoDijkstra nodoActual = cola.poll();
            Parada paradaActual = nodoActual.getParada();
            int tiempoAcumuladoActual = nodoActual.getTiempoAcumulado();

            /**
             * Si el tiempo acumulado para llegar a la parada actual es mayor que el tiempo minimo registrado para esa
             * parada, significa que ya hemos encontrado un camino mas corto para llegar a esa parada, por lo que
             * podemos omitir este nodo y continuar con el siguiente en la cola.
             */
            if (tiempoAcumuladoActual > tiemposMinimos.getOrDefault(paradaActual.getCodigo(), Integer.MAX_VALUE)) {
                continue;
            }

            /**
             * Si ya llegamos a destino, podemos elegir frenar aca
             */
            if (paradaActual.getCodigo() == paradaDestino.getCodigo()) {
                break; //podemos elegir frenar aca, ya que el primer camino que encontramos es el mas corto.
            }

            //Exploramos los tramos vecinos
            for (Tramo tramo : mapaGrafo.obtenerTramosDesde(paradaActual.getCodigo())) {
                if (tramo.getTipo() != 1) continue;
                //LocalTime horaLlegadaAParada = horaLlegada.plusMinutes(tiempoAcumuladoActual);
                int tiempoDelTramo = calcularTiempoTotalTramo(tramo, tiempoAcumuladoActual, diaSemana, horaLlegada, tramos);

                //Si el tiempo total para recorrer el tramo es infinito, significa que no hay colectivos disponibles para ese tramo
                if (tiempoDelTramo != Integer.MAX_VALUE) {
                    int nuevoTiempoTotal = tiempoAcumuladoActual + tiempoDelTramo;
                    int tiempoMejorConocido = tiemposMinimos.getOrDefault(tramo.getFin().getCodigo(), Integer.MAX_VALUE);

                    // Si encontramos un camino más corto a la parada destino del tramo
                    if (nuevoTiempoTotal < tiempoMejorConocido) {
                        tiemposMinimos.put(tramo.getFin().getCodigo(), nuevoTiempoTotal);
                        tramosPadres.put(tramo.getFin().getCodigo(), tramo);
                        cola.add(new NodoDijkstra(tramo.getFin(), nuevoTiempoTotal));
                    }
                }
            }
        }
        // Si encontramos un camino hasta el destino, lo reconstruimos
        if (tramosPadres.containsKey(paradaDestino.getCodigo())) {
            List<Recorrido> mejorCamino = reconstruirCamino(tramosPadres, paradaDestino, tramos);
            soluciones.add(mejorCamino);
        }
        return soluciones;
    }

    /**
     * Extrae las paradas de los tramos disponibles y los almacena en un mapa donde la clave es el codigo
     * de la parada y el valor el objeto de tipo Parada.
     * Lo creamos ya que el el metodo calcularRecorrido recibe como parametros los tramos
     * y entonces vamos a necesitar las paradas.
     * @param tramos
     * @return
     */
    private Map<Integer, Parada> extraerParadaTramo(Map<String, Tramo> tramos) {
        Map<Integer, Parada> paradas = new HashMap<>();
        for (Tramo tramo : tramos.values()) {
            paradas.putIfAbsent(tramo.getInicio().getCodigo(), tramo.getInicio());
            paradas.putIfAbsent(tramo.getFin().getCodigo(), tramo.getFin());
        }
        return paradas;
    }

    /**
     * Encuentra la linea del tramo, verificando que el tramo sea de tipo colectivo y que la parada de fin del tramo
     * @param inicio parada de inicio del tramo
     * @param tramoBuscado del cual queremos encontar la linea
     * @return
     */
    private Linea encontrarLineaDelTramo(Parada inicio, Tramo tramoBuscado, Map<String, Tramo> tramos) {
        //Recorremos todas las lineas disponibles en el sistema
        for (Linea linea : inicio.getLineas()) {
            List<Parada> paradas = linea.getParadas();
            for (int i = 0; i < paradas.size() - 1; i++) {
                if (paradas.get(i).getCodigo() == tramoBuscado.getInicio().getCodigo() &&
                        paradas.get(i + 1).getCodigo() == tramoBuscado.getFin().getCodigo()) {
                    String clave = paradas.get(i).getCodigo() + "-" + paradas.get(i+1).getCodigo() + "-1";
                    if (tramos.containsKey(clave)) {
                        return linea;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Calcula la cantidad de minutos de espera hasta el próximo colectivo disponible.
     * @param horarios Lista de horarios de salida (deben estar ordenados).
     * @param horaLlegadaAEstacion La hora exacta en la que el usuario está listo en la parada.
     * @return Cantidad de minutos de espera, o -1 si no hay más colectivos por ese día.
     */
    private int calcularEspera(List<LocalTime> horarios, LocalTime horaLlegadaAEstacion) {
        if (horarios == null || horarios.isEmpty()) {
            return -1;
        }
        for (LocalTime horarioColectivo : horarios) {
            if (horarioColectivo.isAfter(horaLlegadaAEstacion) || horarioColectivo.equals(horaLlegadaAEstacion)) {
                long espera = ChronoUnit.MINUTES.between(horaLlegadaAEstacion, horarioColectivo);
                return (int) espera;
            }
        }
        return -1;
    }

    /**
     * Reconstruye el camino desde la parada de destino hasta la parada de origen utilizando el mapa de tramos padres,
     * que almacena el tramo utilizado para llegar a cada parada con el tiempo minimo.
     * @param tramosPadres mapa que almacena el tramo utilizado para llegar a cada parada con el tiempo minimo
     * @param paradaDestino parada de destino desde la cual se va a reconstruir el camino hacia la parada de origen
     * @return
     */
    private List<Recorrido> reconstruirCamino(Map<Integer, Tramo> tramosPadres, Parada paradaDestino, Map<String,
            Tramo> tramos) {
        List<Recorrido> camino = new ArrayList<>();
        Tramo tramoActual = tramosPadres.get(paradaDestino.getCodigo());

        while (tramoActual != null) {
            Linea linea = encontrarLineaDelTramo(tramoActual.getInicio(), tramoActual, tramos);
            Recorrido r = new Recorrido();
            r.setLinea(linea); // Aquí se setea la línea encontrada (o null si era a pie)
            r.setOrigen(tramoActual.getInicio());
            r.setDestino(tramoActual.getFin());
            r.setDuracion(tramoActual.getTiempo());
            camino.add(0, r);
            tramoActual = tramosPadres.get(tramoActual.getInicio().getCodigo());
        }
        return camino;
    }

    /**
     * Calcula el tiempo total para recorrer un tramo, teniendo en cuenta el tiempo de espera si el tramo es de tipo
     * colectivo, y el tiempo del tramo.
     * @param tramo tramo del cual queremos calcular el tiempo total para recorrerlo
     * @param tiempoAcumuladoActual tiempo acumulado para llegar a la parada de inicio del tramo, que se va a usar para
     * calcular la hora de llegada
     * @param diaSemana dia de la semana, que se va a usar para obtener los horarios de la linea del tramo si es de
     * tipo colectivo
     * @param horaLlegadaBase hora de llegada base, que se va a usar para calcular la hora de llegada a la parada de
     * inicio del tramo, sumando el tiempo acumulado actual
     * @return
     */
    private int calcularTiempoTotalTramo(Tramo tramo, int tiempoAcumuladoActual, int diaSemana, LocalTime horaLlegadaBase, Map<String, Tramo> tramos) {
        Linea linea = encontrarLineaDelTramo(tramo.getInicio(), tramo, tramos);
        if (linea == null) {
            return tramo.getTiempo();
        }
        List<LocalTime> horarios = linea.obtenerHorariosPorDia(diaSemana);
        LocalTime horaLlegadaAParada = horaLlegadaBase.plusSeconds(tiempoAcumuladoActual);

        int tiempoEspera = calcularEspera(horarios, horaLlegadaAParada);

        if (tiempoEspera == -1) {
            return Integer.MAX_VALUE;
        }
        return tiempoEspera + tramo.getTiempo();
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