package colectivo.negocio;

import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa el calculo de un recorrido, esta clase se va a usar para implementar los algoritmos de
 * recorrido, como el algoritmo de recorrido a pie, el algoritmo de recorrido con transbordo, etc.
 * Esta clase es el contexto ya que se aplico el patron de strategy.
 */
public class Calculo {

    /**
     * Algoritmo de recorrido que se va a usar para calcular el recorrido
     */
    private AlgoritmoRecorrido algoritmo;

    /**
     * Constructor de la clase Calculo, que recibe un algoritmo de recorrido para calcular el recorrido, es decir,
     * se le pasa el algoritmo de recorrido a pie o el algoritmo de recorrido con transbordo, dependiendo
     * de la estrategia
     * @param algoritmo
     */
    public Calculo(AlgoritmoRecorrido algoritmo) {
        this.algoritmo = algoritmo;
    }

    /**
     * Permite cambiar la estrategia de calculo de recorrido en tiempo de ejecucion, es decir, permite cambiar el
     * algoritmo de recorrido a pie por el algoritmo de recorrido con transbordo, o viceversa, sin tener que modificar
     * el codigo de la clase Calculo.
     * @param algoritmo
     */
    public void setEstrategia(AlgoritmoRecorrido algoritmo) {
        this.algoritmo = algoritmo;
    }

    /**
     * Este metodo sera llamado por el Coordinador
     * @param origen
     * @param destino
     * @param diaSemana
     * @param hora
     * @param tramos
     * @return
     */
    public List<List<Recorrido>> ejecutarCalculo(Parada origen, Parada destino, int diaSemana,
                                                 LocalTime hora, Map<String, Tramo> tramos) {
        return algoritmo.calcularRecorrido(origen, destino, diaSemana, hora, tramos);
    }

}