package colectivo.controlador;

import colectivo.aplicacion.ConfiguracionGlobal;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;

import java.util.List;

/**
 * Interfaz que define los métodos que se van a implementar en la clase CoordinadorApp, y que se van a usar para
 * orquestar la inicializacion de la aplicacion, y para coordinar la interaccion entre las diferentes partes de la
 * aplicacion, como el modelo, la vista, y el controlador.
 */
public interface ICoordinadorApp {

    /**
     * Ciclo de vida de la aplicacion.
     */
    void inicializarAplicacion();

    /**
     * Devuelve la configuración global de la aplicación, que incluye los archivos de configuración y de idioma cargados,
     * y proporciona métodos para acceder a ellos.
     * @return La configuración global de la aplicación, que incluye los archivos de configuración y de idioma cargados,
     * y proporciona métodos para acceder a ellos.
     */
    ConfiguracionGlobal getConfiguracion();

    /**
     * Busca una parada por su ID o por su nombre, dependiendo de lo que el usuario haya ingresado en el campo de texto.
     * @param entrada El texto ingresado por el usuario, que puede ser un ID de parada (número) o un nombre de parada (texto).
     * @return La parada correspondiente al ID o nombre ingresado, o null si no se encuentra ninguna parada que coincida con la entrada.
     */
    Parada buscarParada(String entrada);

    /**
     * Valida que el día ingresado por el usuario sea un número entero entre 1 y 7, donde 1 representa el lunes y 7
     * representa el domingo/feriado.
     * @param entrada El día ingresado por el usuario en formato de texto.
     * @return El número entero correspondiente al día de la semana (1-7) si la entrada es válida, o -1 si la entrada
     * no es válida.
     */
    int parsearDia(String entrada);

    /**
     * Valida que la hora ingresada por el usuario tenga el formato correcto (HH:mm) y que sea una hora válida (entre 00:00 y 23:59).
     * @param hora La hora ingresada por el usuario en formato de texto.
     * @return true si la hora es válida, false si no lo es.
     */
    boolean validarHora(String hora);

    /**
     * Ejecuta el cálculo y guarda el resultado en recorridoSolucion.
     */
    void ejecutarCalculo(Parada origen, Parada destino, int dia, String hora);

    /**
     *  Devuelve la lista de paradas para mostrar en la vista, o para usar en otras partes de la aplicación.
     * @return La lista de paradas que se ha cargado desde el modelo, o una lista vacía si no se ha cargado ninguna parada.
     */
    List<Parada> getListaParadas();

    /**
     * Devuelve la lista de recorridos que se ha calculado como solución, o una lista vacía si no se ha calculado ningún recorrido.
     * @return La lista de recorridos que se ha calculado como solución, o una lista vacía si no se ha calculado ningún recorrido.
     */
    List<List<Recorrido>> getRecorridoSolucion();

    /**
     * Cambia el idioma y actualiza el ResourceBundle.
     */
    void cambiarIdioma(String codigoIdioma);

    /**
     * Resetea los resultados de búsqueda (Botón Limpiar).
     */
    void limpiarSistema();
}