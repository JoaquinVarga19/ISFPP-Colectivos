package colectivo.aplicacion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Clase que se encarga de cargar los archivos de configuración y de idioma, y de
 * proporcionar métodos para acceder a ellos.
 */
public class ConfiguracionGlobal {

    /**
     * Aca se define el nombre del archivo de propiedades que se va a cargar, y el nombre del archivo de idioma que se
     * va a cargar.
     */
    private Properties properties;

    /**
     * Se define el ResourceBundle que se va a cargar, y el Locale que se va a usar para cargar el ResourceBundle.
     */
    private ResourceBundle bundle;

    /**
     * Se define el Locale actual que se va a usar para cargar el ResourceBundle, y se inicializa con el Locale por
     * defecto del sistema.
     */
    private Locale localeActual;

    /**
     * Se define un Logger para registrar eventos relacionados con la carga de archivos de configuración y de idioma,
     * y para registrar cualquier error que pueda ocurrir durante este proceso.
     */
    private Logger LOGGER = Logger.getLogger(ConfiguracionGlobal.class.getName());

    /**
     * En el constructor, se inicializan las propiedades, se cargan los archivos de configuración, y se carga el idioma.
     */
    public ConfiguracionGlobal() {
        this.properties = new Properties();
        cargarArchivosConfiguracion();
        // Al inicio, cargamos el idioma por defecto definido en el .properties
        String lenguajeEtiq = properties.getProperty("language", "es");
        cambiarIdioma(lenguajeEtiq);
    }

    /**
     * Cargamos y gestionamos los archivos de aplicacion
     */
    private void cargarArchivosConfiguracion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if (input == null) {
                LOGGER.warning("No se pudo encontrar el archivo de configuración 'config.properties'.");
            } else {
                this.properties.load(input); //carga el archivo de propiedades en el objeto Properties
            }
        } catch (IOException ex) {
            LOGGER.severe("Error al cargar el archivo de configuración 'config.properties': " + ex.getMessage());
        }
    }

    /**
     *Este método es la clave. Permite cambiar el idioma en tiempo de ejecución.
     * @param codigoIdioma Ejemplos: "es", "en", "pt", "fr".
     * El idioma y pais en español estan por defecto.
     * Si el código de idioma es nulo o vacío, se usará el idioma definido en el archivo de configuración (config.properties).
     */
    public void cambiarIdioma(String codigoIdioma) {
        String lang = (codigoIdioma != null && !codigoIdioma.isEmpty())
                ? codigoIdioma
                : properties.getProperty("language", "es");
        String country = properties.getProperty("country", "ES");

        String baseName = "i18n.labels";

        this.localeActual = new Locale(lang, country);

        try {
            this.bundle = ResourceBundle.getBundle(baseName, localeActual);
            LOGGER.info("ResourceBundle cargado con éxito para: " + localeActual);
        } catch (Exception e) {
            LOGGER.severe("ERROR: No se encontró el archivo en resources/i18n/labels" + lang + "_" + country + ".properties");

            this.bundle = ResourceBundle.getBundle("i18n.labels", new Locale("es", "ES"));
        }
    }

    /**
     * Este método es para obtener el texto traducido a partir de una clave, usando el ResourceBundle cargado.
     */
    public String getTexto(String clave) {
        return bundle.getString(clave);
    }

    /**
     * Este método es para obtener el valor de una propiedad a partir de su clave, usando el Properties cargado.
     * @param clave Ejemplo: "nombre.aplicacion", "idioma.actual", etc.
     * @return El valor de la propiedad correspondiente a la clave, o null si no se encuentra.
     */
    public String getProperty(String clave) {
        return properties.getProperty(clave);
    }

    /**
     * Este método es para obtener el Locale actual que se está usando para cargar el ResourceBundle, lo cual puede
     * ser útil para mostrar el idioma actual en la interfaz de usuario o para otras operaciones relacionadas con la
     * localización.
     * @return El Locale actual que se está usando para cargar el ResourceBundle.
     */
    public Locale getLocaleActual() {
        return localeActual;
    }

    /**
     * Este método es para obtener el ResourceBundle cargado, lo cual puede ser útil para acceder a los textos traducidos
     * desde otras partes de la aplicación.
     * @return El ResourceBundle cargado que contiene los textos traducidos para el idioma actual.
     */
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Este método es para obtener el código del idioma actual que se está usando para cargar el ResourceBundle,
     * lo cual puede ser útil para mostrar el idioma actual en la interfaz de usuario o para otras operaciones
     * relacionadas con la localización.
     * @return El código del idioma actual que se está usando para cargar el ResourceBundle, o null si no se encuentra
     * en las propiedades.
     */
    public String getIdiomaActual() {
        return properties.getProperty("idioma.actual");
    }

    /**
     * Este método es para obtener el nombre de la aplicación.
     * @return El nombre de la aplicación definido en las propiedades, o null si no se encuentra.
     */
    public String getNombreAplicacion() {
        return properties.getProperty("nombre.aplicacion");
    }
}