package colectivo.dao.secuencial;

import colectivo.conexion.Factory;
import colectivo.dao.ParadaDAO;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;



/**
 * Clase que implementa la interfaz de TramoDAO para el almacenamiento de tramos en archivos.
 * A su vez se implementan los metodos insertar, actualizar, borrar y buscarTodos.
 * Este se creo despues de ParadaDAOArchivo ya que Tramo depende de Parada.
 */
public class TramoDAOArchivo implements TramoDAO {


    /**
     * Ruta del archivo donde se almacenan los tramos del .txt
     */
    private String rutaArchivo;

    /**
     * Mapa que almacena las paradas disponibles con su codigo como clave y al objeto paradas
     * Este mapa se utiliza para validar las paradas al crear o actualizar tramos.
     */
    private Map<Integer, Parada> paradasDisponibles;

    /**
     * Mapa que almacena los tramos con su codigo como clave y al objeto tramos
     * Este mapa se utiliza para realizar operaciones CRUD sobre los tramos.
     */
    private Map<String, Tramo> tramosMap;

    /**
     * Bandera para indicar si se debe actualizar el archivo al realizar operaciones CRUD
     */
    private boolean actualizar;

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de TramoDAOArchivo
     * Utiliza el logger de Java Util Logging para registrar información, advertencias y errores.
     */
    private static final Logger LOGGER = LogManager.getLogger(TramoDAOArchivo.class);

    /**
     * Constructor de la clase TramoDAOArchivo que inicializa las propiedades necesarias
     * para la gestión de tramos en archivos.
     */
    public TramoDAOArchivo() {
        Properties prop = new Properties();
        try (InputStream imput = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (imput == null) {
                LOGGER.fatal("No se pudo encontrar el archivo config.properties en src");
                throw new IOException("No se encontro el archivo");
            }
            prop.load(imput);
            this.rutaArchivo = prop.getProperty("tramo");

            if ( this.rutaArchivo == null) {
                LOGGER.fatal("Error: No se pudo encontrar la clave 'tramo' en el archivo config.properties");
            }

        } catch (Exception e) {
            LOGGER.fatal("Error: No se pudo leer el archivo config.properties en TramoDAO", e);
        }

        this.paradasDisponibles = cargarParadas();
        this.tramosMap = new LinkedHashMap<>();
        this.actualizar = true;
    }

    /**
     * Inserta un nuevo tramo en el mapa de tramos. El tramo se identifica por la combinación
     * de las paradas de inicio y fin.
     * @param tramo Objeto Tramo a insertar. El tramo se identifica por la combinación de las paradas de inicio y fin.
     */
    @Override
    public void insertar(Tramo tramo) {
        if (tramo != null) {
                String codigoTramo = tramo.getInicio().getCodigo() + "-" + tramo.getFin().getCodigo();
                tramosMap.put(codigoTramo, tramo);
                LOGGER.info("Tramo insertado correctamente: " + codigoTramo);
            } else {
                LOGGER.warn("No se pudo insertar el tramo porque el objeto Tramo es null.");
        }
    }

    /**
     * Actualiza un tramo existente en el mapa de tramos. Si el tramo no existe, se registra una advertencia.
     * @param tramo Objeto Tramo a actualizar.
     * El tramo se identifica por la combinación de las paradas de inicio y fin.
     */
    @Override
    public void actualizar(Tramo tramo) {
        if (tramo != null) {
            String clave = tramo.getInicio().getCodigo() + "-" + tramo.getFin().getCodigo();
            if (tramosMap.containsKey(clave)) {
                tramosMap.put(clave, tramo);
                LOGGER.info("Tramo actualizado correctamente: " + clave);
            } else {
                LOGGER.warn("No se pudo actualizar el tramo porque no existe en el mapa: " + clave);
            }
        }
    }

    /**
     * Borra un tramo del mapa de tramos. Si el tramo no existe, se registra una advertencia.
     * @param tramo Objeto Tramo a borrar. El tramo se identifica por la combinación de las paradas de inicio y fin.
     */
    @Override
    public void borrar(Tramo tramo) {
        if (tramo != null) {
            String clave = tramo.getInicio().getCodigo() + "-" + tramo.getFin().getCodigo();
            if (tramosMap.remove(clave) != null) {
                LOGGER.info("Tramo borrado correctamente: " + clave);
            } else {
                LOGGER.warn("No se pudo borrar el tramo porque no existe en el mapa: " + clave);
            }
        }
    }

    /**
     * Busca y devuelve todos los tramos almacenados en el archivo. Si la bandera de actualizar es verdadera,
     * se recarga el mapa de tramos desde el archivo antes de devolverlo. Si la ruta del archivo no está configurada
     * correctamente, se registra un error y se devuelve un mapa vacío.
     * @return un mapa de tramos con su codigo como clave y el objeto Tramo como valor,
     * o un mapa vacio si ocurre un error.
     */
    @Override
    public Map<String, Tramo> buscarTodos() {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            LOGGER.error("La ruta del archivo de tramos no está configurada correctamente.");
            return Collections.emptyMap();
        }
        if (actualizar) {
            this.tramosMap = leerDelArchivo(rutaArchivo);
             actualizar = false;
            LOGGER.info("Carga de tramos finalizada con éxito. Tramos cargados: {}", this.tramosMap.size());
        }
        return this.tramosMap;
    }

    /**
     * Lee los tramos almacenados en el archivo especificado por la rutaArchivo y los carga en un mapa de tramos.
     * @param ruta la ruta del archivo de tramos a leer
     * @return un mapa de tramos con su codigo como clave y el objeto Tramo como valor,
     * o un mapa vacio si ocurre un error
     */
    private Map<String, Tramo> leerDelArchivo(String ruta) {
        Map<String, Tramo> tramos = new LinkedHashMap<>();

        if (this.paradasDisponibles == null || this.paradasDisponibles.isEmpty()) {
            LOGGER.error("No se pueden cargar los tramos porque no se pudieron cargar las paradas disponibles.");
            return Collections.emptyMap();
        }

        //try (BufferedReader br = new BufferedReader(new FileReader(ruta)))
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ruta);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";");
                int inicio = Integer.parseInt(partes[0].trim());
                int fin = Integer.parseInt(partes[1].trim());
                int tiempo = Integer.parseInt(partes[2].trim());
                int tipo = Integer.parseInt(partes[3].trim());

                Parada paradaInicio = this.paradasDisponibles.get(inicio);
                Parada paradaFin = this.paradasDisponibles.get(fin);

                if (paradaInicio != null && paradaFin != null) {
                    Tramo tramo = new Tramo(paradaInicio, paradaFin, tiempo, tipo);
                    String codigoTramo = inicio + "-" + fin;
                    tramos.put(codigoTramo, tramo);
                } else {
                    LOGGER.warn("No se pudo crear el tramo debido a que no se encontraron las paradas de" +
                            " inicio o fin para el tramo: " + linea);
                }
            }
        } catch (IOException | NumberFormatException e) {
            LOGGER.error("Error al leer el archivo de tramos: " + ruta, e);
            return Collections.emptyMap();
        }
        return tramos;
    }

    /**
     *  Carga las paradas disponibles utilizando el ParadaDAO para obtener todas las paradas almacenadas.
     *  @return un mapa de paradas con su codigo como clave y el
     *  objeto Parada como valor, o un mapa vacio si ocurre un error
     */
    private Map<Integer, Parada> cargarParadas() {
        try {
            ParadaDAO paradaDAO = (ParadaDAO) Factory.getInstancia("PARADA", ParadaDAO.class);
            return paradaDAO.buscarTodos();
        } catch (Exception e) {
            LOGGER.error("Error al obtener ParadaDAO desde la Factory en TramoDAO.", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Obtiene la ruta del archivo donde se almacenan los tramos.
     * @return la ruta del archivo de tramos
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}