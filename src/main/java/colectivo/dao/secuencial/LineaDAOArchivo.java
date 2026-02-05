package colectivo.dao.secuencial;

import colectivo.dao.LineaDAO;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Clase que implementa la interfaz de LineaDAO para el almacenamiento de lineas en archivos.
 * A su vez se implementan los metodos insertar, actualizar, borrar y buscarTodos.
 * Este se creo despues de ParadaDAOArchivo ya que Linea depende de Parada.
 */
public class LineaDAOArchivo implements LineaDAO {

    /**
     * Ruta del archivo donde se almacenan las lineas del .txt
     */
    private String rutaArchivo;

    /**
     * Ruta del archivo donde se almacenan las frecuencias del .txt
     */
    private String rutaArchivoFrecuencias;

    /**
     * Mapa que almacena las paradas disponibles con su codigo como clave y al objeto paradas
     */
    private Map<Integer, Parada> paradasDisponibles;

    /**
     * Mapa que almacena las lineas con su codigo como clave y al objeto lineas
     */
    private Map<String, Linea> lineasMap;

    /**
     * Bandera para indicar si se debe actualizar el archivo al realizar operaciones CRUD
     */
    private boolean actualizar;

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de LineaDAOArchivo
     */
    private static final Logger LOGGER = LogManager.getLogger(LineaDAOArchivo.class);

    /**
     * Constructor de la clase LineaDAOArchivo que inicializa las propiedades necesarias
     * para la gestión de lineas en archivos.
     * Recibe como parametro un mapa de paradas ya cargadas para relacionarlas con las lineas.
     */
    public LineaDAOArchivo(Map<Integer, Parada> paradas) {
        this.paradasDisponibles = paradas;
        this.lineasMap = new LinkedHashMap<>();
        this.actualizar = false;

        Properties prop = new Properties();
        try (InputStream imput = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (imput != null) {
                prop.load(imput);
                this.rutaArchivo = prop.getProperty("lineas");
                this.rutaArchivoFrecuencias = prop.getProperty("frecuencias");
                LOGGER.info("Configuracion de lineas cargadas correctamente");
            }
        } catch (IOException e) {
            LOGGER.fatal("Error: No se pudo cargar el archivo config.properties en LineaDAO", e);
        }
    }

    /**
     *
     */
    @Override
    public void insertar(Linea linea) {

    }

    /**
     *
     */
    @Override
    public void actualizar(Linea linea) {

    }

    /**
     *
     */
    @Override
    public void borrar(Linea linea) {

    }

    /**
     *
     */
    @Override
    public Map<String, Linea> buscarTodos() {
        return Map.of();
    }

    /**
     *
     */
    private Map<Integer, Parada> cargarParadas() {
        return Map.of();
    }

    /**
     * Lee las lineas desde el archivo especificado por la ruta
     * @param ruta la ruta del archivo de lineas
     * @return un mapa con las lineas leidas del archivo
     */
    private Map<String, Linea> leerDelArchivo(String ruta) {
        Map< String, Linea> lineas = new LinkedHashMap<>();


        return lineas;
    }

    /**
     *
     */
    private void escribirEnArchivo() {

    }

    /**
     * Obtiene la ruta del archivo de lineas
     * @return la ruta del archivo de lineas
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}