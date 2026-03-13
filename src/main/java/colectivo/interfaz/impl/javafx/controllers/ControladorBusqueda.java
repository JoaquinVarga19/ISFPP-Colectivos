package colectivo.interfaz.impl.javafx.controllers;

import colectivo.aplicacion.Constantes;
import colectivo.controlador.CoordinadorApp;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Controlador para la pantalla de búsqueda de rutas en la aplicación.
 * Este controlador se encarga de manejar las interacciones del usuario
 * en la pantalla de búsqueda, como seleccionar origen, destino, día y hora,
 * y mostrar los resultados de las consultas.
 */
public class ControladorBusqueda {

    /**
     * Logger para registrar eventos y errores en el controlador de búsqueda.
     */
    private static final Logger LOGGER = LogManager.getLogger(ControladorBusqueda.class);

    /**
     * Referencias a los elementos de la interfaz (ComboBox's, Button's y VBox para resultados).
     */
    private ComboBox<String> comboOrigen;
    private ComboBox<String> comboDestino;
    private ComboBox<String> comboDia;
    private ComboBox<String> comboHora;
    private Button btnCalcular;
    private Button btnLimpiar;
    private VBox vboxResultados;

    /**
     * Controlador del mapa para manejar la visualización de rutas en la pantalla de búsqueda.
     */
    private ControladorMapa controladorMapa;

    /**
     * Referencia al coordinador de la aplicación para solicitar cálculos y acceder a datos.
     */
    private CoordinadorApp coordinador;

    /**
     * Mapa para almacenar las paradas por su ID, facilitando la búsqueda de información de paradas a partir de su código.
     */
    private Map<Integer, Parada> mapaParadas = new HashMap<>();

    /**
     * Estos atributos los usamos para almacenar paradas y dias, con el fin de que cuando busque parada de origen y
     * destino y el dia, el buscador me de recomendaciones por referencia tipo Google cuando quiero buscar Facebook
     */
    private List<String> listaParadasOriginales = new ArrayList<>();
    private List<String> listaDiasOriginales = new ArrayList<>();

    public void setCoordinadorApp(CoordinadorApp coordinador) {
        this.coordinador = coordinador;
    }

    /**
     * Constructor del controlador de búsqueda, que recibe las referencias a los elementos de la interfaz y el controlador del mapa.
     * @param origen
     * @param destino
     * @param dia
     * @param hora
     * @param resultados
     * @param mapa
     */
    public ControladorBusqueda(ComboBox<String> origen, ComboBox<String> destino, ComboBox<String> dia, ComboBox<String> hora,
                               VBox resultados, ControladorMapa mapa) {
        this.comboOrigen = origen;
        this.comboDestino = destino;
        this.comboDia = dia;
        this.comboHora = hora;
        this.vboxResultados = resultados;
        this.controladorMapa = mapa;

        inicializarCombosEstaticos();
    }

    /**
     * Método para inicializar los ComboBox's de días y horas con valores estáticos.
     */
    private void inicializarCombosEstaticos() {
        LOGGER.info("Inicializando combos estáticos (Días y Horas)...");
        listaDiasOriginales.clear();
        listaDiasOriginales.addAll(Arrays.asList("1 - Lunes", "2 - Martes", "3 - Miércoles", " 4 - Jueves", "5 - Viernes", "6 - Sábado",
                "7 - Domingo/Feriado"));

        configurarBuscador(comboDia, listaDiasOriginales);


        comboHora.setEditable(true); //permitimos que el usuario escriba la hora
        comboHora.setPromptText("HH:mm");
        comboHora.getItems().addAll("08:00", "12:00", "16:00", "20:00");
    }

    /**
     * Método para cargar las paradas en los ComboBox's de origen y destino, a partir de un mapa de paradas.
     * usamos un List ya que al llamar a coordinador.getListaParadas() nos dio error
     * le cargamos objetos Parada para tener toda la info de cada parada, pero para mostrar en el comboBox solo
     * mostramos "ID - Dirección" para que el usuario pueda elegir fácilmente.
     * @param paradas
     */
    public void cargarParadas(List<Parada> paradas) {
        listaParadasOriginales.clear();
        mapaParadas.clear();
        for (Parada parada : paradas) {
            listaParadasOriginales.add(parada.getCodigo() + " - " + parada.getDireccion());
            mapaParadas.put(parada.getCodigo(), parada);
        }

        // Configuramos los combos de origen y destino para que sean "Buscadores"
        configurarBuscador(comboOrigen, listaParadasOriginales);
        configurarBuscador(comboDestino, listaParadasOriginales);
    }

    /**
     * Configuramos el buscador para generar autocompletados.
     * @param comboBox
     * @param elementosOriginales
     */
    private void configurarBuscador(ComboBox<String> comboBox, List<String> elementosOriginales) {
        comboBox.getItems().setAll(elementosOriginales);
        comboBox.setEditable(true);

        // Le ponemos una "oreja" (Listener) que escucha cada vez que el usuario teclea algo
        comboBox.getEditor().textProperty().addListener((observable, textoViejo,
                                                         textoNuevo) -> {
            // Si el nuevo texto está vacío, mostramos todos los elementos originales
            if (textoNuevo == null || textoNuevo.isEmpty()) {
                comboBox.getItems().setAll(elementosOriginales);
                return;
            }

            // Lista temporal para guardar los elementos que coinciden con el texto nuevo
            List<String> elementosFiltrados = new ArrayList<>();
            for (String item : elementosOriginales) {
                if (item.toLowerCase().contains(textoNuevo.toLowerCase())) {
                    elementosFiltrados.add(item);
                }
            }

            // Actualizamos el ComboBox con los elementos filtrados
            javafx.application.Platform.runLater(() -> {
                comboBox.getItems().setAll(elementosFiltrados);
                comboBox.show();
            });
        });
    }

    /**
     * * Recopila los datos de los ComboBox, valida que estén completos y
     * solicita el cálculo de la ruta al sistema central.
     * */
    public void procesarBusqueda() {
        LOGGER.info("Iniciando procesamiento de búsqueda de ruta...");

        String textoOrigen = comboOrigen.getEditor().getText();
        String textoDestino = comboDestino.getEditor().getText();
        String textoDia = comboDia.getEditor().getText();
        String textoHora = comboHora.getEditor().getText();
        
        if (textoOrigen.isEmpty() || textoDestino.isEmpty() || textoDia.isEmpty() || textoHora.isEmpty()) {
            LOGGER.warn("El usuario dejo campos vacios.");
            mostrarMensajeEnPantalla("Faltan completar campos. Por favor, revise formulario", true);
            // Más adelante podemos hacer que acá salte un cartelito rojo en la pantalla
            return;
        }

        int idOrigen = extraerIdDeTextoInteligente(textoOrigen, listaParadasOriginales);
        int idDestino = extraerIdDeTextoInteligente(textoDestino, listaParadasOriginales);
        int idDia = extraerIdDeTextoInteligente(textoDia, listaDiasOriginales);

        if (idOrigen == -1 || idDestino == -1 || idDia == -1) {
            mostrarMensajeEnPantalla("ERROR: Por favor, seleccione opciones válidas para origen, destino y día.", true);
            return;
        }

        if (idOrigen == idDestino) {
            mostrarMensajeEnPantalla("ERROR: El origen y el destino no pueden ser el mismo.", true);
            return;
        }

        mostrarMensajeEnPantalla("¡Validación exitosa! Buscando ruta de" + idOrigen + " a " + idDestino + "...", false);

        /**
         *Llamamos a la cocina (coordinador) para que haga el trabajo pesado de calcular la ruta, horarios, etc. y
         * luego mostrarlo en pantalla.
         */
        Parada paradaOrigen = mapaParadas.get(idOrigen);
        Parada paradaDestino = mapaParadas.get(idDestino);
        try {
            mostrarMensajeEnPantalla("Calculando ruta, por favor espere...", false);

            coordinador.ejecutarCalculo(paradaOrigen, paradaDestino, idDia, textoHora);

            List<List<Recorrido>> soluciones = coordinador.getRecorridoSolucion();

            mostrarTarjetasDeResultados(soluciones); // (Para la Etapa 3)

        } catch (Exception e) {
            LOGGER.error("Error al calcular: " + e.getMessage(), e);
            mostrarMensajeEnPantalla("Ocurrió un error al buscar la ruta. Por favor, intente nuevamente.", true);
        }
    }

    /**
     * Método auxiliar para extraer el código de parada del texto seleccionado en el ComboBox.
     * ejemplo: si el texto es "123 - Av. Siempre Viva 742", este método debería devolver "123".
     * Ahora lo hacemos de forma inteligente. Si el texto está incompleto, busca la coincidencia
     * en las listas originales para deducir qué
     * opción quería el usuario.
     * @param texto
     * @return
     */
    private int extraerIdDeTextoInteligente(String texto, List<String> opcionesOriginales) {
        if (texto == null || texto.trim().isEmpty()) {
            return -1;
        }

        try {
            String[] partes = texto.split(" - ");
            return Integer.parseInt(partes[0].trim());
        } catch (Exception e) {
            for (String opcion : opcionesOriginales) {
                if (opcion.toLowerCase().contains(texto.toLowerCase())) {
                    try {
                        String[] partesOpcion = opcion.split(" - ");
                        return Integer.parseInt(partesOpcion[0].trim());
                    } catch (Exception ignored) {}
                }
            }
        }
        LOGGER.warn("No se pudo deducir el ID para el texto escrito: " + texto);
        return -1;
    }

    /**
     * Limpia los valores seleccionados en los menús y vacía el contenedor de resultados.
     * De tal forma que el usuario pueda realizar una nueva consulta desde cero sin tener que reiniciar la aplicación.
     * Solamente el usuario aprieta el boton de "Limpiar Interfaz" y se limpia la consola, el mapa, los resultados y
     * los campos de búsqueda.
     */
    public void limpiarFormulario() {
        LOGGER.info("Limpiando formulario de búsqueda e interfaz de resultados...");

        comboOrigen.getEditor().clear();
        comboDestino.getEditor().clear();
        comboDia.getEditor().clear();
        comboHora.getEditor().clear();

        comboOrigen.getItems().setAll(listaParadasOriginales);
        comboDestino.getItems().setAll(listaParadasOriginales);
        comboDia.getItems().setAll(listaDiasOriginales);

        vboxResultados.getChildren().clear();
        // controladorMapa.limpiarRutas(); // (Para la Etapa 3)
    }

    /**
     * Mostramos un mensaje en pantalla respecto a los resultados, ya sea un error o un resultado exitoso. Este método
     * se puede usar para mostrar mensajes de error (en rojo) o mensajes de éxito (en verde).
     * @param mensaje
     * @param error
     */
    private void mostrarMensajeEnPantalla(String mensaje, boolean error) {
        vboxResultados.getChildren().clear();

        Label etiqueta = new Label(mensaje);
        etiqueta.setWrapText(true);

        if (error) {
            etiqueta.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            etiqueta.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
        }

        vboxResultados.getChildren().add(etiqueta);
    }

    /**
     * Dibuja las tarjetas visuales de resultados de recorridos.
     * @param soluciones
     */
    private void mostrarTarjetasDeResultados(List<List<Recorrido>> soluciones) {
        vboxResultados.getChildren().clear();

        if (soluciones == null || soluciones.isEmpty()) {
            mostrarMensajeEnPantalla("No se encontraron rutas disponibles para los parámetros seleccionados.", true);
            return;
        }

        int contadorTransbordo = 0;
        int contadorDirecto = 0;

        for (List<Recorrido> opcion : soluciones) {
            if (opcion == null || opcion.isEmpty()) {
                continue;
            }

            Set<String> lineasUsadas = new HashSet<>();
            boolean esCaminando = false;

            for (Recorrido r : opcion) {
                if (r.getLinea() == null || r.getLinea().getNombre().equalsIgnoreCase("Caminando")) {
                    esCaminando = true;
                } else {
                    lineasUsadas.add(r.getLinea().getNombre());
                }
            }

            String tipoRuta;
            String colorTarjeta;
            String textoTitulo;

            if (esCaminando) {
                tipoRuta = "Caminando";
                colorTarjeta = Constantes.COLOR_CAMINANDO;
                textoTitulo = "Caminando";
            } else if (lineasUsadas.size() > 1) {
                tipoRuta = "Con Transbordo";
                contadorTransbordo++;
                colorTarjeta = (contadorTransbordo == 1) ? Constantes.COLOR_DIJKSTRA_1 :
                        (contadorTransbordo == 2) ? Constantes.COLOR_DIJKSTRA_2 : Constantes.COLOR_DIJKSTRA_3;
                textoTitulo = "Lineas " + String.join(" - ", lineasUsadas) + " (Con Transbordo)";
            } else {
                tipoRuta = "Directo";
                contadorDirecto++;
                colorTarjeta = (contadorDirecto == 1) ? Constantes.COLOR_DIRECTO_1 :
                        (contadorDirecto == 2) ? Constantes.COLOR_DIRECTO_2 : Constantes.COLOR_DIRECTO_3;
                textoTitulo = "Linea " + lineasUsadas.iterator().next() + " (Directo)";
            }

            //Maqueta de la tarjeta
            VBox tarjeta = new VBox();
            tarjeta.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 10px; " +
                    "-fx-background-radius: 10px; -fx-padding: 15px; -fx-spacing: 5px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            VBox.setMargin(tarjeta, new javafx.geometry.Insets(10, 0, 15, 0));

            //titulo de color
            Label lblTitulo = new Label(textoTitulo);
            lblTitulo.setStyle("-fx-background-color: " + colorTarjeta + "; -fx-text-fill: #ffffff; " +
                    "-fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
            tarjeta.getChildren().add(lblTitulo);

            //Datos tarjeta
            String direccionOrigen = opcion.get(0).getOrigen().getDireccion();
            String direccionDestino = opcion.get(opcion.size() - 1).getDestino().getDireccion();
            tarjeta.getChildren().add(crearFilaInformacion("Paradas:", direccionOrigen + " → " + direccionDestino));

            //hora salida
            tarjeta.getChildren().add(crearFilaInformacion("Hora de salida:", opcion.get(0).getHoraSalida().toString()));

            //Duracion total
            int duracionTotalSegundos = 0;
            for (Recorrido r : opcion) {
                duracionTotalSegundos += r.getDuracion();
            }
            int minutos = duracionTotalSegundos / 60;
            tarjeta.getChildren().add(crearFilaInformacion("Duración total:", minutos + " min"));

            //agregar tarjeta a pantalla
            vboxResultados.getChildren().add(tarjeta);
        }
    }

    /**
     * Método auxiliar para crear las filas de texto, le damos color a la etiqueta y al valor, y lo usamos para mostrar
     * la información de cada tramo en las tarjetas de resultados.
     * @return
     */
    private HBox crearFilaInformacion(String etiqueta, String valor) {
        HBox fila = new HBox();
        fila.setSpacing(5);

        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: #666666;");

        fila.getChildren().addAll(lblEtiqueta, lblValor);
        return fila;
    }
}