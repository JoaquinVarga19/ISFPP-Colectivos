package colectivo.dao.secuencial;

import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.dao.ParadaDAO;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.Collections;
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
     */
    public LineaDAOArchivo() {
        Properties prop = new Properties();
        try (InputStream imput = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (imput == null) {
                LOGGER.fatal("No se pudo encontrar el archivo config.properties en src");
                throw new IOException("No se encontro el archivo");
            }

            /*Carga las propiedades desde el archivo de configuración*/
            prop.load(imput);

            /*Obtiene la ruta del archivo de lineas desde las propiedades cargadas*/
            this.rutaArchivo = prop.getProperty("linea");
            this.rutaArchivoFrecuencias = prop.getProperty("frecuencia");

            if (this.rutaArchivo == null || this.rutaArchivoFrecuencias == null) {
                LOGGER.fatal("Error: No se pudieron encontrar las claves 'linea' y 'frecuencia' " +
                        "en el archivo config.properties");
            }


        } catch (IOException e) {
            LOGGER.error("Error: No se pudo leer el archivo config.properties en LineaDAO", e);
        }

        this.paradasDisponibles = cargarParadas();
        this.lineasMap = new LinkedHashMap<>();
        this.actualizar = true;
    }

    /**
     * Inserta una nueva linea en el archivo.
     * @param linea Objeto Linea a insertar
     */
    @Override
    public void insertar(Linea linea) {
        if (linea != null) {
            lineasMap.put(linea.getCodigo(), linea);
            LOGGER.info("Linea insertada: " + linea.getCodigo() + "en memoria");
        } else {
            LOGGER.warn("No se pudo insertar: La linea no existe en el mapa");
        }
    }

    /**
     * Actualiza una linea existente en el archivo. si este existe en el mapa de lineas,
     * la pisa con los nuevos datos
      * y guarda en el archivo
     * @param linea Objeto Linea a actualizar
     */
    @Override
    public void actualizar(Linea linea) {
        if (linea != null && lineasMap.containsKey(linea.getCodigo())) {
            lineasMap.put(linea.getCodigo(), linea);
            LOGGER.info("Linea actualizada: " + linea.getCodigo() + "en memoria");
        } else {
            LOGGER.warn("No se pudo actualizar: La linea no existe en el mapa");
        }
    }

    /**
     * Borra una linea del archivo.
     * @param linea Objeto Linea a borrar
     */
    @Override
    public void borrar(Linea linea) {
        if (linea != null && lineasMap.containsKey(linea.getCodigo())) {
            lineasMap.remove(linea.getCodigo());
            LOGGER.info("Linea borrada: " + linea.getCodigo() + "en memoria");
        } else {
            LOGGER.warn("No se pudo borrar: La linea no existe en el mapa");
        }
    }

    /**
     * Busca todas las lineas almacenadas en el archivo y las devuelve como un mapa.
     * Si la bandera de actualizar es verdadera, se recarga el mapa de lineas desde el archivo.
     */
    @Override
    public Map<String, Linea> buscarTodos() {
        if (this.rutaArchivo == null || this.rutaArchivoFrecuencias == null) {
            LOGGER.warn("Error: No se pudo encontrar la ruta del archivo de lineas o frecuencias por que son nulas");
            return Collections.emptyMap();
        }

        if (actualizar) {
            this.lineasMap = leerDelArchivo(rutaArchivo);
            this.actualizar = false;
            LOGGER.info("Carga de líneas finalizada con éxito. Líneas cargadas: {}", this.lineasMap.size());
        }
        return this.lineasMap;
    }

    /**
     * Carga las paradas disponibles utilizando el ParadaDAO para obtener todas las paradas almacenadas.
     *
     * @return un mapa de paradas con su codigo como clave y el
     * objeto Parada como valor, o un mapa vacio si ocurre un error
     */
    private Map<Integer, Parada> cargarParadas() {
        try {
            ParadaDAO paradaDAO = (ParadaDAO) Factory.getInstancia("PARADA", ParadaDAO.class);
            return paradaDAO.buscarTodos();
        } catch (Exception e) {
            LOGGER.error("Error al obtener ParadaDAO desde la Factory en LineaDAO.", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Lee las lineas desde el archivo especificado por la ruta
     * @param ruta la ruta del archivo de lineas
     * @return un mapa con las lineas leidas del archivo
     */
    private Map<String, Linea> leerDelArchivo(String ruta) {
        Map< String, Linea> lineas = new LinkedHashMap<>();

        /**
         *  Verifica si el mapa de paradas disponibles es nulo o esta vacio, si es asi se registra una advertencia
         *  y se retorna un mapa vacio
         */
        if (this.paradasDisponibles == null || this.paradasDisponibles.isEmpty()) {
            LOGGER.warn("Error: No se pudieron cargar las paradas necesarias para leer las líneas.");
            return Collections.emptyMap();
        }

        /*
         * Intenta leer el archivo de lineas utilizando un InputStream y un BufferedReader.
         * Para cada linea leida, se divide en partes utilizando la coma como separador.
         */
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(this.rutaArchivo);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String lineaTexto;
            while ((lineaTexto = br.readLine()) != null) {
                if (lineaTexto.trim().isEmpty()) {
                    continue;
                }
                String[] partes = lineaTexto.split(";");
                String codigo = partes[0].trim();
                String nombre = partes[1].trim();
                Linea lineaObj = new Linea(codigo, nombre);

                for (int i = 2; i < partes.length; i++) {
                    int codigoParada = Integer.parseInt(partes[i].trim());
                    Parada parada = this.paradasDisponibles.get(codigoParada);
                    if (parada != null) {
                        lineaObj.agregarParada(parada);
                    }
                }
                lineas.put(codigo, lineaObj);
            }

        } catch (IOException e) {
            LOGGER.error("Error al leer archivo de líneas: " + rutaArchivo, e);
        }

        /*
         * Hacemos lo mismo que el paso anterior pero con las frecuencias
         */
        try (InputStream isFreq = getClass().getClassLoader().getResourceAsStream(rutaArchivoFrecuencias);
             BufferedReader brFreq = new BufferedReader(new InputStreamReader(isFreq))) {

            String lineaTexto;
            while ((lineaTexto = brFreq.readLine()) != null) {
                if (lineaTexto.trim().isEmpty()) {
                    continue;
                }
                String[] partes = lineaTexto.split(";");
                String codigoLinea = partes[0].trim();
                int diaSemana = Integer.parseInt(partes[1].trim());
                LocalTime hora = LocalTime.parse(partes[2].trim());

                Linea lineaExistente = lineas.get(codigoLinea);
                if (lineaExistente != null) {
                    lineaExistente.agregarFrecuencia(diaSemana, hora);
                }
            }

        } catch (IOException | RuntimeException e) {
            LOGGER.error("Error al leer archivo de frecuencias: " + rutaArchivoFrecuencias, e);
        }
        return lineas;
    }

    /**
     * Obtiene la ruta del archivo de lineas
     * @return la ruta del archivo de lineas
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}