package colectivo.aplicacion;

/**
 * Clase que contiene las constantes utilizadas en la aplicacion, comunmente lo utiliza
 * la clase Tramo para definir el tipo de tramo ya sea colectivo o caminando
 */
public class Constantes {
    public static final int COLECTIVO = 1;
    public static final int CAMINANDO = 2;

    // Nuevas constantes para la "Línea" ficticia de la estrategia
    public static final String CODIGO_LINEA_PEATON = "PEATON";
    public static final String NOMBRE_LINEA_PEATON = "A PIE";

    // =====================================================================
    // DISEÑO DE RESULTADOS (MOCKUP)
    // =====================================================================

    // Con Transbordo (Dijkstra)
    public static final String COLOR_DIJKSTRA_1 = "#f12a2a"; // Rojo
    public static final String COLOR_DIJKSTRA_2 = "#7ed957"; // Verde
    public static final String COLOR_DIJKSTRA_3 = "#4e6ddb"; // Azul

    // Directo
    public static final String COLOR_DIRECTO_1 = "#ff5c00"; // Naranja
    public static final String COLOR_DIRECTO_2 = "#e120c8"; // Fucsia
    public static final String COLOR_DIRECTO_3 = "#52d8b4"; // Turquesa

    // Caminando
    public static final String COLOR_CAMINANDO = "#edb900"; // Amarillo oro
}
