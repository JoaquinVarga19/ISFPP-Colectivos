package colectivo.interfaz.impl.javafx.controllers;

import colectivo.aplicacion.ConfiguracionGlobal;
import colectivo.aplicacion.Constantes;
import colectivo.controlador.CoordinadorApp;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    //private Button btnCalcular; las comentamos ya que no las usamos
    //private Button btnLimpiar;
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

    /**
     *  ResourceBundle para manejar la internacionalización de la interfaz, permitiendo mostrar textos en
     *  diferentes idiomas según la configuración del usuario.
     */
    private ResourceBundle bundle;

    /**
     * Configuración global para acceder a las propiedades y al idioma configurado en la aplicación.
     */
    private ConfiguracionGlobal configIdioma = new ConfiguracionGlobal();

    public void setCoordinadorApp(CoordinadorApp coordinador) {
        this.coordinador = coordinador;
    }

    /**
     * Constructor del controlador de búsqueda, que recibe las referencias a los elementos de la interfaz y el controlador del mapa.
     * @param origen Referencia al ComboBox de origen.
     * @param destino Referencia al ComboBox de destino.
     * @param dia Referencia al ComboBox de día.
     * @param hora Referencia al ComboBox de hora.
     * @param resultados Referencia al VBox donde se mostrarán los resultados de la búsqueda.
     * @param mapa Referencia al controlador del mapa para manejar la visualización de rutas en la pantalla de búsqueda.
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

        ConfiguracionGlobal configIdioma = new ConfiguracionGlobal();
        this.bundle = configIdioma.getBundle();
    }

    /**
     * Método para inicializar los ComboBox's de días y horas con valores estáticos.
     */
    private void inicializarCombosEstaticos() {
        LOGGER.info("Inicializando combos estáticos (Días y Horas)...");
        listaDiasOriginales.clear();

        // Concatenamos el número (que necesita tu lógica) con el día traducido del properties
        listaDiasOriginales.addAll(Arrays.asList(
                "1 - " + configIdioma.getTexto("dia.1"),
                "2 - " + configIdioma.getTexto("dia.2"),
                "3 - " + configIdioma.getTexto("dia.3"),
                "4 - " + configIdioma.getTexto("dia.4"),
                "5 - " + configIdioma.getTexto("dia.5"),
                "6 - " + configIdioma.getTexto("dia.6"),
                "7 - " + configIdioma.getTexto("dia.7")
        ));

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
     * Hacemos verificacion de cada letra que el usuario escribe, y filtramos la lista de opciones para mostrar
     * solo las que coincidan con lo escrito.
     * @param comboBox El ComboBox al que queremos convertir en buscador.
     * @param elementosOriginales La lista original de elementos que se mostrarán en el ComboBox, para poder filtrarla
     * según lo que el usuario escriba.
     */
    private void configurarBuscador(ComboBox<String> comboBox, List<String> elementosOriginales) {
        comboBox.getItems().setAll(elementosOriginales);
        comboBox.setEditable(true);

        comboBox.getEditor().textProperty().addListener((observable, textoViejo,
                                                         textoNuevo) -> {

            if (textoNuevo == null) {
                return;
            }

            if (elementosOriginales.contains(textoNuevo)) {
                return;
            }

            if (textoNuevo.isEmpty()) {
                comboBox.getItems().setAll(elementosOriginales);
                return;
            }

            List<String> filtrados = new ArrayList<>();
            for (String item : elementosOriginales) {
                if (item.toLowerCase().contains(textoNuevo.toLowerCase())) {
                    filtrados.add(item);
                }
            }

            Platform.runLater(() -> {

                String textoActual = comboBox.getEditor().getText();
                comboBox.getItems().setAll(filtrados);
                comboBox.getEditor().setText(textoActual);

                if (!filtrados.isEmpty() && comboBox.getScene() != null && comboBox.getScene().getWindow().isFocused()) {
                    comboBox.show();
                }

            });
        });
    }

    /**
     * * Recopila los datos de los ComboBox, valida que estén completos y sean correctos, y luego llama al coordinador
     * para calcular la ruta entre el origen y destino seleccionados.
     * */
    public void procesarBusqueda() {
        LOGGER.info("Iniciando procesamiento de búsqueda de ruta...");

        String textoOrigen = comboOrigen.getEditor().getText();
        String textoDestino = comboDestino.getEditor().getText();
        String textoDia = comboDia.getEditor().getText();
        String textoHora = comboHora.getEditor().getText();
        
        if (textoOrigen.isEmpty() || textoDestino.isEmpty() || textoDia.isEmpty() || textoHora.isEmpty()) {
            LOGGER.warn("El usuario dejo campos vacios.");
            mostrarMensajeEnPantalla(configIdioma.getTexto("msg.error.campos_vacios"), true);
            return;
        }

        int idOrigen = extraerIdDeTextoInteligente(textoOrigen, listaParadasOriginales);
        int idDestino = extraerIdDeTextoInteligente(textoDestino, listaParadasOriginales);
        int idDia = extraerIdDeTextoInteligente(textoDia, listaDiasOriginales);

        if (idOrigen == -1 || idDestino == -1 || idDia == -1) {
            mostrarMensajeEnPantalla(configIdioma.getTexto("msg.error.sin_rutas"), true);
            return;
        }

        if (idOrigen == idDestino) {
            mostrarMensajeEnPantalla(configIdioma.getTexto("msg.error.origen_destino_iguales"), true);
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

            mostrarTarjetasDeResultados(soluciones, textoHora);

        } catch (Exception e) {
            LOGGER.error("Error al calcular: " + e.getMessage(), e);
            mostrarMensajeEnPantalla(configIdioma.getTexto("msg.error.calculo") + " " + e.getMessage(), true);
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
            String soloNumeros = partes[0].replaceAll("[^0-9]", "");

            if (!soloNumeros.isEmpty()) {
                return Integer.parseInt(soloNumeros);
            }
        } catch (Exception ignored) {}

        for (String opcion : opcionesOriginales) {
            if (opcion.toLowerCase().contains(texto.toLowerCase().trim())) {
                try {
                    String[] partes = opcion.split(" - ");
                    String soloNumerosOpcion = partes[0].replaceAll("[^0-9]", "");
                    if (!soloNumerosOpcion.isEmpty()) {
                        return Integer.parseInt(soloNumerosOpcion);
                    }
                } catch (Exception ignored) {}
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
        controladorMapa.limpiarRutas();
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
     * Dibuja las tarjetas visuales de resultados de recorridos detallando paso a paso.
     * Cada tarjeta representa una opción de ruta encontrada, mostrando información como las líneas utilizadas, paradas
     * de origen y destino, horarios de salida y llegada, duración de cada tramo, tiempo total del viaje, etc.
     * Tambien hacemos un puente con javascript para conectar los colores de las tarjetitas con el mapa.
     * @param soluciones La lista con las opciones de rutas encontradas.
     * @param textoHora La hora elegida por el usuario en el buscador (Ej: "17:00").
     */
    private void mostrarTarjetasDeResultados(List<List<Recorrido>> soluciones, String textoHora) {
        vboxResultados.getChildren().clear();

        if (controladorMapa != null) {
            controladorMapa.limpiarRutas();
        }

        if (soluciones == null || soluciones.isEmpty()) {
            mostrarMensajeEnPantalla(configIdioma.getTexto("msg.error.sin_rutas"), true);
            return;
        }

        LocalTime horaIngresada;
        try {
            horaIngresada = LocalTime.parse(textoHora);
        } catch (Exception e) {
            horaIngresada = LocalTime.of(0,0);
        }
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        int contadorTransbordo = 0;
        int contadorDirecto = 0;
        int numeroOpcion = 1;

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

            String colorTarjeta;
            String textoTitulo;

            if (esCaminando) {
                colorTarjeta = Constantes.COLOR_CAMINANDO;
                textoTitulo = configIdioma.getTexto("card.caminando");
            } else if (lineasUsadas.size() > 1) {
                contadorTransbordo++;
                colorTarjeta = (contadorTransbordo == 1) ? Constantes.COLOR_DIJKSTRA_1 :
                        (contadorTransbordo == 2) ? Constantes.COLOR_DIJKSTRA_2 : Constantes.COLOR_DIJKSTRA_3;
                textoTitulo = configIdioma.getTexto("card.lineas") + " " + String.join(" - ", lineasUsadas) + " " + configIdioma.getTexto("card.transbordo");
            } else {
                contadorDirecto++;
                colorTarjeta = (contadorDirecto == 1) ? Constantes.COLOR_DIRECTO_1 :
                        (contadorDirecto == 2) ? Constantes.COLOR_DIRECTO_2 : Constantes.COLOR_DIRECTO_3;
                textoTitulo = configIdioma.getTexto("card.linea") + " " + lineasUsadas.iterator().next() + " " + configIdioma.getTexto("card.directo");
            }

            VBox tarjetaOpcion = new VBox();
            tarjetaOpcion.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 10px; " +
                    "-fx-background-radius: 10px; -fx-padding: 15px; -fx-spacing: 15px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            VBox.setMargin(tarjetaOpcion, new javafx.geometry.Insets(10, 0, 25, 0));

            Label lblTitulo = new Label(bundle.getString("card.opcion") + "" + numeroOpcion + " | " + textoTitulo);
            lblTitulo.setStyle("-fx-background-color: " + colorTarjeta + "; -fx-text-fill: #ffffff; " +
                    "-fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 5px;");
            tarjetaOpcion.getChildren().add(lblTitulo);

            VBox contenedorTramos = new VBox();
            contenedorTramos.setSpacing(10);

            LocalTime reloj = horaIngresada;
            int duracionTotalSegundos = 0;

            for (Recorrido r : opcion) {
                VBox miniTarjeta = new VBox();

                miniTarjeta.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 10 0;");

                String nombreLinea;
                if (r.getLinea() == null || r.getLinea().getNombre().equalsIgnoreCase("card.caminando")) {
                    nombreLinea = configIdioma.getTexto("card.caminando");
                } else {
                    nombreLinea = configIdioma.getTexto("card.linea") + " " + r.getLinea().getNombre();
                }

                miniTarjeta.getChildren().add(crearFilaInformacion(configIdioma.getTexto("card.linea") + ":", nombreLinea));

                String origenTramo = r.getOrigen().getDireccion();
                String destinoTramo = r.getDestino().getDireccion();
                miniTarjeta.getChildren().add(crearFilaInformacion(configIdioma.getTexto("card.paradas"), origenTramo + " → " + destinoTramo));

                miniTarjeta.getChildren().add(crearFilaInformacion(configIdioma.getTexto("card.hora_salida"), reloj.format(formatoHora)));

                reloj = reloj.plusSeconds(r.getDuracion());

                miniTarjeta.getChildren().add(crearFilaInformacion(configIdioma.getTexto("card.hora_llegada"), reloj.format(formatoHora)));

                int minTramo = r.getDuracion() / 60;
                miniTarjeta.getChildren().add(crearFilaInformacion(configIdioma.getTexto("card.duracion"), minTramo + " " + configIdioma.getTexto("card.min")));

                contenedorTramos.getChildren().add(miniTarjeta);
                duracionTotalSegundos += r.getDuracion();
            }

            tarjetaOpcion.getChildren().add(contenedorTramos);

            VBox footer = new VBox();
            footer.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10px; -fx-background-radius: 5px; -fx-spacing: 5px;");

            int horasTotales = duracionTotalSegundos / 3600;
            int minTotales = (duracionTotalSegundos % 3600) / 60;
            String textoTiempoTotal = String.format("%02d:%02d", horasTotales, minTotales);

            Label lblTotalViaje = new Label(configIdioma.getTexto("card.tiempo_total") + " " + textoTiempoTotal + " " + configIdioma.getTexto("card.hs") + " <<");
            lblTotalViaje.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

            Label lblLlegadaFinal = new Label(configIdioma.getTexto("card.llegada_final") + " " + reloj.format(formatoHora) + " <<");
            lblLlegadaFinal.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

            footer.getChildren().addAll(lblTotalViaje, lblLlegadaFinal);
            tarjetaOpcion.getChildren().add(footer);

            vboxResultados.getChildren().add(tarjetaOpcion);

            StringBuilder coordsBuilder = new StringBuilder();

            for (Recorrido r : opcion) {
                coordsBuilder.append(r.getOrigen().getLatitud()).append(",")
                        .append(r.getOrigen().getLongitud()).append(";");
            }

            Recorrido ultimoTramo = opcion.get(opcion.size() - 1);
            coordsBuilder.append(ultimoTramo.getDestino().getLatitud()).append(",")
                    .append(ultimoTramo.getDestino().getLongitud());

            String datosParaMapa = coordsBuilder.toString() + "|" + colorTarjeta;

            if (controladorMapa != null) {
                controladorMapa.dibujarRutas(datosParaMapa);
            }

            numeroOpcion++;
        }
    }

    /**
     * Método auxiliar para crear las filas de texto, le damos color a la etiqueta y al valor, y lo usamos para mostrar
     * la información de cada tramo en las tarjetas de resultados.
     * @return HBox (fila) con la etiqueta y el valor formateados.
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