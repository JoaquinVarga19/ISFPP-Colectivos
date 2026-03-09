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

    // --- Ciclo de Vida y Configuración ---
    void inicializarAplicacion();
    ConfiguracionGlobal getConfiguracion();

    // --- Búsqueda Inteligente (ID o Nombre) ---
    Parada buscarParada(String entrada);
    int parsearDia(String entrada);
    boolean validarHora(String hora);


    // --- Lógica de Negocio ---
    /**
     * Ejecuta el cálculo y guarda el resultado en recorridoSolucion.
     */
    void ejecutarCalculo(Parada origen, Parada destino, int dia, String hora);

    // --- Acceso a Datos para la UI ---
    List<Parada> getListaParadas();
    List<List<Recorrido>> getRecorridoSolucion();

    // --- Gestión de Idioma ---
    /**
     * Cambia el idioma y actualiza el ResourceBundle.
     */
    void cambiarIdioma(String codigoIdioma);

    /**
     * Resetea los resultados de búsqueda (Botón Limpiar).
     */
    void limpiarSistema();

}
