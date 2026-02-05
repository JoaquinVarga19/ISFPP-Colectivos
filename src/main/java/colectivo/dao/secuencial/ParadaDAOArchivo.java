package colectivo.dao.secuencial;

import colectivo.dao.ParadaDAO;
import colectivo.modelo.Parada;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
//import java.util.logging.Logger;

/**
 * Clase que implementa la interfaz de ParadaDAO para el almacenamiento de paradas en archivos.
 * A su vez se implementan los metodos insertar, actualizar, borrar y buscarTodos.
 */
public class ParadaDAOArchivo implements ParadaDAO {

    /**
     * Ruta del archivo donde se almacenan las paradas del .txt
     */
    private String rutaArchivo;

    /**
     * Mapa que almacena las paradas con su ID como clave y al objeto paradas
     */
    private Map<Integer, Parada> paradasMap;

    /**
     * Bandera para indicar si se debe actualizar el archivo al realizar operaciones CRUD
     */
    private boolean actualizar;

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de ParadaDAOArchivo
     */
    private static final Logger LOGGER = LogManager.getLogger(ParadaDAOArchivo.class);

    /**
     * Constructor de la clase ParadaDAOArchivo que inicializa las propiedades necesarias
     * para la gestión de paradas en archivos.
     */
    public ParadaDAOArchivo() {
        Properties prop = new Properties();
        try (InputStream imput = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (imput == null) {
                LOGGER.fatal("No se pudo encontrar el archivo config.properties en src");
            }

            /*Carga las propiedades desde el archivo de configuración*/
            prop.load(imput);

            /*Obtiene la ruta del archivo de paradas desde las propiedades cargadas*/
            this.rutaArchivo = prop.getProperty("paradas");

            if ( this.rutaArchivo == null) {
                LOGGER.fatal("Error: No se pudo encontrar la clave 'parada' en el archivo config.properties");
            }

        } catch (IOException ex) {
            LOGGER.fatal("Error: No se pudo leer el archivo config.properties en ParadaDAO", ex);
        }

        this.paradasMap = new LinkedHashMap<>();
        this.actualizar = true;
    }

    /**
     * Metodo para insertar una nueva parada en el archivo.
     * @param parada Objeto Parada a insertar
     * llama al metodo escribirEnArchivo para persistencia de datos
     */
    @Override
    public void insertar(Parada parada) {
        if (parada != null) {
            paradasMap.put(parada.getCodigo(), parada);
            LOGGER.info("Parada insertada: " + parada.getCodigo() + "en memoria");
        }
        escribirEnArchivo();
    }

    /**
     * Actualiza una parada existente en el archivo. si este existe en el mapa de paradas, la pisa con los nuevos datos
     * y guarda en el archivo
     * @param parada Objeto Parada a actualizar
     */
    @Override
    public void actualizar(Parada parada) {
        if (parada != null && paradasMap.containsKey(parada.getCodigo())) {
            paradasMap.put(parada.getCodigo(), parada);
            LOGGER.info("Parada actualizada: " + parada.getCodigo() + "en memoria");
        escribirEnArchivo();
        } else {
            LOGGER.warn("No se pudo actualizar la parada. No existe la parada con codigo: "
                    + (parada != null ? parada.getCodigo() : "null"));
        }
    }

    /**
     * Borra una parada del archivo.
     * @param parada Objeto Parada a borrar
     */
    @Override
    public void borrar(Parada parada) {
        if (parada != null) {
            Parada eliminada = paradasMap.remove(parada.getCodigo());

            if (eliminada != null) {
                LOGGER.info("Parada ID:"  + parada.getCodigo() + "eliminada de la memoria.");
                escribirEnArchivo();
            } else {
                LOGGER.warn("No se pudo eliminar la parada. No existe la parada con codigo: " + parada.getCodigo());
            }
        }
    }

    /**
     * Busca todas las paradas almacenadas en el archivo y las devuelve como un mapa.
     * @return Mapa de paradas con su codigo como clave y el objeto Parada como valor
     */
    @Override
    public Map<Integer, Parada> buscarTodos() {
        if (actualizar) {
            this.paradasMap = leerDelArchivo(rutaArchivo);
            this.actualizar = false;
        }
        return this.paradasMap;
    }

    /**
     * lee las paradas desde el archivo especificado pasandole como parametro la ruta
     * @param ruta Ruta del archivo de paradas
     * @return Mapa de paradas leidas desde el archivo
     */
    private Map<Integer, Parada> leerDelArchivo(String ruta) {
        LOGGER.info("Comenzando a leer paradas desde el recurso: " + ruta);
        Map<Integer, Parada> paradas = new LinkedHashMap<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ruta)) {

            if (is == null) {
                LOGGER.error("No se pudo encontrar el archivo de paradas en resources: " + ruta);
                return Collections.emptyMap();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) {
                        continue;
                    }

                    try {
                        String[] partes = linea.split(";");
                        int codigo = Integer.parseInt(partes[0].trim());
                        String direccion = partes[1].trim();
                        double latitud = Double.parseDouble(partes[2].trim());
                        double longitud = Double.parseDouble(partes[3].trim());

                        Parada parada = new Parada(codigo, direccion, latitud, longitud);
                        paradas.put(codigo, parada);
                    } catch (Exception e) {
                        LOGGER.warn("Línea omitida por error de formato: " + linea);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Error al procesar el archivo de paradas: " + ruta, ex);
            return Collections.emptyMap();
        }
        return paradas;
    }

    /**
     * Escrbe las paradas actuales en el archivo especificado con el fin de persistencia de datos
     * para que estos no se pierdan al cerrar la aplicacion.
     */
    private void escribirEnArchivo() {
        LOGGER.info("Guardando cambios en el archivo: " + rutaArchivo);
        try (PrintWriter pw = new PrintWriter(new FileWriter("src/main/resources" + rutaArchivo))) {

            for (Parada p : paradasMap.values()) {
                String linea = String.format("%d;%s;%s;%s\n",
                        p.getCodigo(),
                        p.getDireccion(),
                        p.getLatitud(), p.getLongitud());
                pw.println(linea);
            }

            this.actualizar = false;
            LOGGER.info("Cambios guardados exitosamente en el archivo. Total de lineas: " + paradasMap.size());


        } catch (IOException ex) {
            LOGGER.error("Error al escribir en el archivo de paradas: " + rutaArchivo, ex);
        }
    }

    /**
     * Obtiene la ruta del archivo donde se almacenan las paradas
     * @return Ruta del archivo de paradas
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}