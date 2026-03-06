package colectivo.controlador;

import colectivo.aplicacion.ConfiguracionGlobal;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;

import java.util.List;
import java.util.Map;

/**
 * Interfaz que define los métodos que se van a implementar en la clase CoordinadorApp, y que se van a usar para
 * orquestar la inicializacion de la aplicacion, y para coordinar la interaccion entre las diferentes partes de la
 * aplicacion, como el modelo, la vista, y el controlador.
 */
public interface ICoordinadorApp {
    
    //Metodos de carga de datos
    Map<Integer, Parada> cargarParadas();
    Map<String, Linea> cargarLineas();

    //Metodos de calculo
    List<List<Recorrido>> calcularRecorrido(Parada origen, Parada destino, int dia, int hora);
    
    //Metodos de configuracion
    // Getters necesarios para el flujo de datos
    ConfiguracionGlobal getConfiguracion();
    List<Parada> getListaParadas();
    List<List<Recorrido>> getRecorridoSolucion();
}
