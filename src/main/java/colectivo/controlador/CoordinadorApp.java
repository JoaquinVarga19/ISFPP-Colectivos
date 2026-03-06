package colectivo.controlador;


import colectivo.aplicacion.ConfiguracionGlobal;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;
import colectivo.negocio.Calculo;
import colectivo.servicio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Clase que se encargra de orquestar la inicializacion de la aplicacion, y de coordinar la interaccion entre las
 * diferentes partes de la aplicacion, como el modelo, la vista, y el controlador.
 */
public class CoordinadorApp implements ICoordinadorApp{

    private static final Logger LOGGER = LogManager.getLogger(CoordinadorApp.class);
    private ParadaService paradaService;
    private TramoService tramoService;
    private LineaService lineaService;
    private InterfazService interfazService;
    private ConfiguracionGlobal configuracion;
    private Calculo calculo;
    private Map<String, Tramo> mapaTramos;
    private Map<Integer, Parada> mapaParadas;
    private Map<String, Linea> mapaLineas;
    List<List<Recorrido>> recorridoSolucion;

    /**
     *
     */
    public void inicializarAplicacion() {

    }

    /**
     *
     * @param interfazService
     */
    public void asignarInterfaz(InterfazService interfazService) {

    }


    @Override
    public Map<Integer, Parada> cargarParadas() {
        return Map.of();
    }

    @Override
    public Map<String, Linea> cargarLineas() {
        return Map.of();
    }

    @Override
    public List<List<Recorrido>> calcularRecorrido(Parada origen, Parada destino, int dia, int hora) {
        return List.of();
    }

    @Override
    public ConfiguracionGlobal getConfiguracion() {
        return null;
    }

    @Override
    public List<Parada> getListaParadas() {
        return List.of();
    }

    @Override
    public List<List<Recorrido>> getRecorridoSolucion() {
        return List.of();
    }

    public void buscarParada(String nombreParada) {

    }




}