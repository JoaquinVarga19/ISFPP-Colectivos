package colectivo.controlador;


import colectivo.aplicacion.ConfiguracionGlobal;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;
import colectivo.negocio.Calculo;
import colectivo.negocio.CalculoCaminando;
import colectivo.negocio.CalculoDijkstra;
import colectivo.negocio.CalculoDirecto;
import colectivo.servicio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Clase que se encargra de orquestar la inicializacion de la aplicacion, y de coordinar la interaccion entre las
 * diferentes partes de la aplicacion, como el modelo, la vista, y el controlador.
 */
public class CoordinadorApp implements ICoordinadorApp{

    /**
     * Logger para registrar eventos y errores relacionados con la inicialización de la aplicación, la carga de datos,
     * y la ejecucion del calculo.
     */
    private static final Logger LOGGER = LogManager.getLogger(CoordinadorApp.class);

    /**
     * Servicio para gestionar las paradas, incluyendo la carga de datos y la búsqueda de paradas por ID o nombre.
     */
    private ParadaService paradaService;

    /**
     * Servicio que gestiona los tramos que van desde una parada a otro.
     */
    private TramoService tramoService;

    /**
     * Servicio que gestiona las lineas, incluyendo la carga de datos y la búsqueda de lineas por ID o nombre.
     */
    private LineaService lineaService;

    /**
     * Servicio que gestiona la interfaz de usuario, ya sea consola o JavaFX, dependiendo de la implementación que se elija.
     */
    private InterfazService interfazService;

    /**
     * Configuración global de la aplicación, que incluye la carga de archivos de configuración y de idioma,
     * y métodos para aceder a ellos.
     */
    private ConfiguracionGlobal configuracion;

    /**
     * Servicio que se encarga de ejecutar el cálculo del recorrido, utilizando los datos de paradas, tramos, y lineas.
     */
    private Calculo calculo;

    /**
     * Mapa que almacena los tramos, con la clave siendo una combinación de origen y
     * destino (por ejemplo, "ParadaA-ParadaB") para facilitar la búsqueda de tramos entre dos paradas.
     */
    private Map<String, Tramo> mapaTramos;

    /**
     * Mapa que almacena las paradas, con el ID de la parada como clave y valor el objeto Parada.
     */
    private Map<Integer, Parada> mapaParadas;

    /**
     * Mapa que almacena las lineas, con el ID de la linea como clave y valor el objeto Linea.
     */
    private Map<String, Linea> mapaLineas;

    /**
     * Lista de soluciones de recorrido que se obtiene al ejecutar el cálculo. Cada solución es una lista de objetos
     * Recorrido, que representan los tramos y lineas que el usuario debe tomar para ir desde el origen al destino,
     * incluyendo los tiempos de espera y transbordos.
     */
    List<List<Recorrido>> recorridoSolucion;

    /**
     * Inicializa la aplicación, cargando la configuración global, inicializando los servicios, y cargando los datos
     * de paradas, tramos, y lineas.
     */
    @Override
    public void inicializarAplicacion() {
        try {
            LOGGER.info("Iniciando la secuencia de apertura de la aplicación...");

            this.configuracion = new ConfiguracionGlobal();

            inicializarServicios();

            cargarDatosUnaVez();

            this.calculo = new Calculo(new CalculoDijkstra());

            this.recorridoSolucion = new ArrayList<>();

            if (this.interfazService != null) {
                if (this.interfazService instanceof Coordinable) {
                    ((Coordinable) this.interfazService).setCoordinadorApp(this);
                }
                LOGGER.info("Lanzando la interfaz de usuario...");
                this.interfazService.iniciar();
            }

        } catch (Exception e) {
            LOGGER.fatal("Error crítico durante la inicialización de la aplicación: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Aca se usa a la Factory para instanciar las implementaciones. Es el "puente" con la persistencia.
     * Implementamos los servicios.
     * La app no sabe que tipo de persistencia usa, si es archivo de texto o base de datos.
     */
    private void inicializarServicios() {
        LOGGER.info("Inicializando servicios...");
        try {
            this.paradaService = new ParadaServiceImpl();
            this.tramoService = new TramoServiceImpl();
            this.lineaService = new LineaServiceImpl();
            this.interfazService = new InterfazServiceImpl();
            LOGGER.info("Servicios inicializados correctamente.");
        } catch (Exception e) {
            LOGGER.fatal("Error al inicializar servicios: " + e.getMessage(), e);
            throw new RuntimeException("Error al inicializar servicios: " + e.getMessage(), e);
        }
    }

    /**
     * Llama a buscarTodos() de cada servicio y guarda los resultados en tus atributos
     * mapaParadas, mapaLineas y mapaTramos.
     */
    private void cargarDatosUnaVez() {
        LOGGER.info("Cargando datos...");
        try {
            this.mapaParadas = paradaService.buscarTodos();
            LOGGER.info("Se cargaron {} paradas.", mapaParadas.size());
            this.mapaTramos = tramoService.buscarTodos();
            LOGGER.info("Se cargaron {} tramos.", mapaTramos.size());
            this.mapaLineas = lineaService.buscarTodos();
            LOGGER.info("Se cargaron {} lineas.", mapaLineas.size());

            LOGGER.info("Datos cargados correctamente.");
        } catch (Exception e) {
            LOGGER.fatal("Error al cargar datos: " + e.getMessage(), e);
            throw new RuntimeException("Error al cargar datos: " + e.getMessage(), e);
        }
    }

    /**
     * Devuelve la configuración global de la aplicación, que incluye los textos traducidos y las propiedades cargadas
     * desde el archivo de configuración.
     * @return ConfiguracionGlobal
     */
    @Override
    public ConfiguracionGlobal getConfiguracion() {
        return this.configuracion;
    }

    /**
     * Busca una parada por ID o por nombre, dependiendo de lo que el usuario haya ingresado. Si el usuario
     * ingresa un número, se busca por ID. Si ingresa texto, se busca por nombre.
     * @param entrada La entrada del usuario, que puede ser un ID numérico o un nombre de parada.
     * @return La parada encontrada, o null si no se encuentra ninguna parada que coincida con la entrada.
     */
    @Override
    public Parada buscarParada(String entrada) {

        if (entrada == null || entrada.isEmpty()) {
            LOGGER.warn("Entrada vacía para buscar parada.");
            return null;
        }

        try {
            int id = Integer.parseInt(entrada.trim());
            return mapaParadas.get(id);
        } catch (NumberFormatException e) {
            return mapaParadas.values().stream()
                    .filter(parada -> parada.getDireccion().equalsIgnoreCase(entrada.trim()))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Parsea el día ingresado por el usuario. Si es un número, se devuelve ese número. Si es un texto,
     * se convierte a un número según el día de la semana (Lunes=1, Martes=2, ..., Domingo/feriado=7). Si no se
     * reconoce el formato, se devuelve -1.
     * @param entrada La entrada del usuario, que puede ser un número del 1 al 7 o el nombre de un día de la semana.
     * @return El número del día de la semana (1-7) o -1 si la entrada no es válida.
     */
    @Override
    public int parsearDia(String entrada) {
        if (entrada == null || entrada.isEmpty()) {
            LOGGER.warn("Entrada vacía para parsear día.");
            return -1;
        }
        String dia = entrada.trim().toLowerCase();
        if (dia.matches("[1-7]")) {
            return Integer.parseInt(dia);
        }

        if (dia.equals(configuracion.getTexto("dia.1").toLowerCase())) return 1;
        if (dia.equals(configuracion.getTexto("dia.2").toLowerCase())) return 2;
        if (dia.equals(configuracion.getTexto("dia.3").toLowerCase())) return 3;
        if (dia.equals(configuracion.getTexto("dia.4").toLowerCase())) return 4;
        if (dia.equals(configuracion.getTexto("dia.5").toLowerCase())) return 5;
        if (dia.equals(configuracion.getTexto("dia.6").toLowerCase())) return 6;
        if (dia.equals(configuracion.getTexto("dia.7").toLowerCase())) return 7;

        return -1;
    }

    /**
     * Valida que la hora ingresada por el usuario tenga el formato correcto (HH:mm) y que sea una hora válida
     * (00:00 a 23:59). Si la hora es válida, devuelve true. Si no es válida, devuelve false.
     * @param hora La hora ingresada por el usuario, que debe tener el formato HH:mm.
     * @return true si la hora es válida, false si no es válida.
     */
    @Override
    public boolean validarHora(String hora) {
        if (hora == null || !hora.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            LOGGER.warn(configuracion.getTexto("Entrada vacía para validar hora."));
            return false;
        }
        return true;
    }

    /**
     * Ejecuta el cálculo del recorrido utilizando el servicio de cálculo. Valida que los parámetros no sean nulos o inválidos
     * antes de ejecutar el calculo.
     * @param origen La parada de origen seleccionada por el usuario.
     * @param destino La parada de destino seleccionada por el usuario.
     * @param dia El día de la semana seleccionado por el usuario, representado como un número del 1 al 7.
     * @param hora La hora de inicio del recorrido.
     */
    @Override
    public void ejecutarCalculo(Parada origen, Parada destino, int dia, String hora) {
        if (origen == null || destino == null) {
            LOGGER.warn("Origen y destino nulos.");
            return;
        }
        try {
            LocalTime hs = LocalTime.parse(hora);
            List<List<Recorrido>> todasLasSoluciones = new ArrayList<>();
            LOGGER.info("Caminando");
            this.calculo.setEstrategia(new CalculoCaminando());
            List<List<Recorrido>> rutasCaminando = this.calculo.ejecutarCalculo(origen, destino, dia, hs, this.mapaTramos);
            if (rutasCaminando != null && !rutasCaminando.isEmpty()) {
                todasLasSoluciones.addAll(rutasCaminando);
            }

            if (origen.getCodigo() != destino.getCodigo() ) {
                if (compartenLinea(origen, destino)) {
                    LOGGER.info("Directo (sin transbordo)");
                    this.calculo.setEstrategia(new CalculoDirecto());
                    List<List<Recorrido>> rutasDirectas = this.calculo.ejecutarCalculo(origen, destino, dia, hs, this.mapaTramos);
                    if (rutasDirectas != null && !rutasDirectas.isEmpty()) {
                        todasLasSoluciones.addAll(rutasDirectas);
                    }
                }
            }

            LOGGER.info("Con transbordo (Dijkstra)");
            this.calculo.setEstrategia(new CalculoDijkstra());
            List<List<Recorrido>> rutasConTransbordo = this.calculo.ejecutarCalculo(origen, destino, dia, hs, this.mapaTramos);

            if (rutasConTransbordo != null && !rutasConTransbordo.isEmpty()) {
                todasLasSoluciones.addAll(rutasConTransbordo);
            }

            this.recorridoSolucion = todasLasSoluciones;

        } catch (Exception e) {
            LOGGER.error("Error al ejecutar cálculo: " + e.getMessage(), e);
            throw new RuntimeException("Error al calcular el recorrido: " + e.getMessage());
        }
    }

    /**
     * Devuelve la lista de paradas para mostrar en la UI, esta lista se obtiene del mapaParadas que se cargó al inicio
     * de la aplicación. Si el mapaParadas está vacío, devuelve una lista vacía.
     * @return Lista de paradas para mostrar en la UI.
     */
    @Override
    public List<Parada> getListaParadas() {
        if (mapaParadas == null || mapaParadas.isEmpty()) {
            LOGGER.warn("No hay paradas cargadas para mostrar.");
            return new ArrayList<>();
        }
        return new ArrayList<>(mapaParadas.values());
    }

    /**
     * Devuelve la lista de soluciones de recorrido que se obtuvo al ejecutar el cálculo.
     * @return Lista de soluciones de recorrido para mostrar en la UI, o null si no se ha ejecutado ningún cálculo aún.
     */
    @Override
    public List<List<Recorrido>> getRecorridoSolucion() {
        return this.recorridoSolucion;
    }

    /**
     * Cambia el idioma de la aplicación utilizando el método cambiarIdioma de la clase ConfiguracionGlobal.
     * Este método se llama cuando el usuario selecciona un nuevo idioma en la UI.
     * El método actualizará el ResourceBundle con el nuevo idioma y notificará a la UI para que actualice los
     * textos mostrados en pantalla.
     * @param codigoIdioma El código del idioma seleccionado por el usuario (por ejemplo, "es" para español,
     *"en" para inglés, etc.).
     */
    @Override
    public void cambiarIdioma(String codigoIdioma) {
        LOGGER.info("Cambiando idioma a: " + codigoIdioma);
        configuracion.cambiarIdioma(codigoIdioma);
    }

    /**
     * Resetea los resultados de búsqueda, es decir, limpia la lista de soluciones de recorrido y cualquier otro estado
     * que deba ser reiniciado para una nueva búsqueda.
     * Este método se llama cuando el usuario hace clic en el botón "Limpiar" en la UI.
     */
    @Override
    public void limpiarSistema() {
        this.recorridoSolucion = null;
        LOGGER.info("Sistema limpiado. Listo para una nueva búsqueda.");
    }

    /**
     * Verifica si existe alguna linea de colectivo que pase tanto por origen como destino.
     * @param origen La parada de origen seleccionada por el usuario.
     * @param destino La parada de destino seleccionada por el usuario.
     * @return true si existe al menos una linea que pase por ambas paradas, false si no existe ninguna linea que las
     * conecte directamente.
     */
    private boolean compartenLinea(Parada origen, Parada destino) {
        if (origen.getLineas() != null && destino.getLineas() != null) {
            for (Linea lineaOrigen : origen.getLineas()) {
                for (Linea lineaDestino : destino.getLineas()) {
                    if (lineaOrigen.getNombre().equals(lineaDestino.getNombre())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}