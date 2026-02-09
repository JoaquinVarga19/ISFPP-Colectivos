package colectivo.conexion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase Factory que se encarga de crear instancias de los distintos DAO
 * Es mas que nada por si quiero cambiar el tipo de almacenamiento de los DAO,
 * si en el dia de mañana quiero cambiar desde el archivo de texto a base de datos
 * solo tengo que cambiar el archivo factory.properties y listo, no tengo que tocar nada mas del codigo.
 */
public class Factory {

    /**
     * Mapa concurrente que almacena las instancias de los distintos DAO,
     * con el nombre del DAO como clave y la instancia como valor.
     */
    private static final ConcurrentHashMap<String, Object> INSTANCIAS = new ConcurrentHashMap<>();

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de LineaDAOArchivo
     */
    private static final Logger LOGGER = LogManager.getLogger(Factory.class);

    /**
     * Constructor privado para evitar instanciación
     */
    private Factory() {
        throw new AssertionError("No se pueden instanciar objetos de la clase Factory");
    }

    /**
     * Méto-do genérico para obtener una instancia de un DAO específico.
     * Utiliza el mét-odo computeIfAbsent del mapa INSTANCIAS para crear la instancia si no existe,
     * o devolver la existente si ya fue creada.
     * @param objectName
     * @param expectedType
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstancia(String objectName, Class<T> expectedType) {
        Object instancia = INSTANCIAS.computeIfAbsent( objectName, Factory::crearInstancia);
        if (!expectedType.isInstance(instancia)) {
            String error = String.format(" La instancia para '%s' no es del tipo esperado. Tipo actual: %s, Tipo esperado: %s",
                    objectName, instancia.getClass().getName(), expectedType.getName());
            LOGGER.fatal(error);
            throw new ClassCastException(error);
        }
        return (T) instancia;
    }

    /**
     * Mét-odo privado que se encarga de crear una instancia de un DAO específico utilizando reflexión.
     * @param clave nombre del DAO para el cual se desea crear la instancia,
     *             debe coincidir con la clave en el archivo factory.properties
     * @return la instancia creada del DAO correspondiente a la clave proporcionada
     */
    private static Object crearInstancia(String clave) {
        try {
            LOGGER.info("Creando instancia para: {}", clave);
            ResourceBundle rb = ResourceBundle.getBundle("factory");

            if (!rb.containsKey(clave)) {
                LOGGER.fatal("No se encontró la clave en factory.properties {}", clave);
                throw new IllegalArgumentException("Clave no encontrada en factory.properties: " + clave);
            }

            String className = rb.getString(clave);
            Object instancia = Class.forName(className).getDeclaredConstructor().newInstance();

            LOGGER.info("Instancia creada exitosamente para: {} -> {}", clave, className);
            return instancia;

        } catch (Exception e) {
            LOGGER.fatal("Error al crear instancia para {}: {}", clave, e.getMessage(), e);
                throw new RuntimeException("Error al crear instancia para " + clave, e);
        }
    }

    /**
     * Mét-odo público para limpiar la cache de instancias, eliminando todas las entradas del mapa INSTANCIAS.
     * Esto puede ser útil para liberar memoria o forzar la creación de nuevas instancias en futuras llamadas
     * a getInstancia.
     */
    public static void limpiarCache() {
        INSTANCIAS.clear();
        LOGGER.info("Cache de instancias limpiada exitosamente");
    }

    /**
     * Mét-odo público para forzar la creación de una nueva instancia de un DAO específico,
     * eliminando la instancia existente
     * @param objectName nombre del DAO para el cual se desea crear una nueva instancia,
     * @param expectedType tipo esperado de la instancia, utilizado para asegurar que la nueva instancia creada
     * sea del tipo correcto
     * @return la nueva instancia creada del DAO correspondiente a la clave proporcionada
     * @param <T> tipo genérico que representa el tipo esperado de la instancia del DAO, utilizado para
     * asegurar que la nueva instancia creada sea del tipo correcto
     */
    public static <T> T resubirInstancia(String objectName, Class<T> expectedType) {
        INSTANCIAS.remove(objectName);
        return getInstancia(objectName, expectedType);
    }
}


