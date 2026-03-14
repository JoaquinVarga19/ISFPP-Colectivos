package colectivo.interfaz.impl.javafx.controllers;

import colectivo.aplicacion.ConfiguracionGlobal;
import colectivo.controlador.Coordinable;
import colectivo.controlador.CoordinadorApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla principal de la aplicación.
 * Este controlador se encarga de manejar las interacciones del usuario
 * en la pantalla principal, como seleccionar origen, destino, día y hora,
 * y mostrar los resultados de las consultas.
 */
public class ControladorPantallaPrincipal implements Initializable, Coordinable {

    /**
     * Etiquetas para mostrar los resultados de la consulta (origen, destino, día, hora y resultado de la consulta).
     */
    @FXML private Label nombreApp;
    @FXML private Label parOri;
    @FXML private Label parDes;
    @FXML private Label diia;
    @FXML private Label horaa;
    @FXML private Label resRecor;


    /**
     * Combobox's para seleccionar el origen, destino, día y hora de la consulta.
     */
    @FXML private ComboBox<String> comboOrigen;
    @FXML private ComboBox<String> comboDestino;
    @FXML private ComboBox<String> comboDia;
    @FXML private ComboBox<String> comboHora;

    /**
     * Botones para calcular la ruta y limpiar la interfaz.
     */
    @FXML private Button btnCalcular;
    @FXML private Button btnLimpiar;

    /**
     * VBox para mostrar los resultados de la consulta (dentro del ScrollPane).
     */
    @FXML private VBox vboxResultados; // El VBox adentro de tu ScrollPane

    // WebView para mostrar el mapa con la ruta calculada
    @FXML private WebView mapaWebView;

    /**
     * Controlador del mapa para manejar la visualización de rutas en el WebView.
     */
    private ControladorMapa controladorMapa;

    /**
     * Controlador de búsqueda para manejar la lógica de consulta de rutas y horarios.
     */
    private ControladorBusqueda controladorBusqueda;

    /**
     * Coordinador de la aplicación para interactuar con el resto de la arquitectura.
     */
    private CoordinadorApp coordinador;


    /**
     * Aca recibimos el coordinador de la aplicación para poder interactuar con el resto de la arquitectura.
     * Ahora agregamos setCoordinador() para pasarle el coordinador al mesero (ControladorBusqueda) y que pueda hacer su trabajo.
     * @param coordinadorApp El coordinador de la aplicación que se le pasa a este controlador para que pueda
     * interactuar con el resto de la arquitectura. Este método es parte de la interfaz Coordinable y se llama desde el
     * coordinador principal de la aplicación después de crear este controlador.
     */
    @Override
    public void setCoordinadorApp(CoordinadorApp coordinadorApp) {
        this.coordinador = coordinadorApp;
        System.out.println("Coordinador recibido en ControladorPantallaPrincipal.");

        if (this.controladorBusqueda != null) {
            this.controladorBusqueda.setCoordinadorApp(coordinadorApp);
            this.controladorBusqueda.cargarParadas(coordinador.getListaParadas());
        }
    }

    /**
     * Metodo de la interfaz Initializable que se llama automáticamente al cargar la pantalla. Aquí puedes inicializar
     * tus componentes, cargar datos, etc.
     * @param url URL de la ubicacion del archivo FXML que se esta cargando.
     * @param resourceBundle Recursos adicionales que se pueden usar para internacionalización, aunque en este caso no
     * los estamos usando directamente porque tenemos nuestra propia clase de configuración global para manejar el idioma.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Inicializando interfaz");

        ConfiguracionGlobal configIdioma = new ConfiguracionGlobal();

        nombreApp.setText(configIdioma.getTexto("nombre.aplicacion"));
        parOri.setText(configIdioma.getTexto("label.origen.largo"));
        parDes.setText(configIdioma.getTexto("label.destino.largo"));
        diia.setText(configIdioma.getTexto("label.dia.largo"));
        horaa.setText(configIdioma.getTexto("label.hora.largo"));
        resRecor.setText(configIdioma.getTexto("label.resultados"));

        btnCalcular.setText(configIdioma.getTexto("btn.calcular"));
        btnLimpiar.setText(configIdioma.getTexto("btn.limpiar"));

        comboOrigen.setPromptText(configIdioma.getTexto("label.origen"));
        comboDestino.setPromptText(configIdioma.getTexto("label.destino"));
        comboDia.setPromptText(configIdioma.getTexto("label.dia"));
        comboHora.setPromptText(configIdioma.getTexto("label.hora"));

        this.controladorMapa = new ControladorMapa(mapaWebView);

        this.controladorBusqueda = new ControladorBusqueda(
                comboOrigen, comboDestino, comboDia, comboHora,
                vboxResultados, controladorMapa
        );
    }

    /**
     * Le decimos al mesero que haga su trabajo.
     * @param actionEvent El evento que se genera al hacer clic en el botón "Calcular Ruta".
     */
    @FXML
    public void calcularRuta(ActionEvent actionEvent) {
        controladorBusqueda.procesarBusqueda();
    }

    /**
     * Limpiamos la interfaz para una nueva consulta.
     * @param actionEvent El evento que se genera al hacer clic en el botón "Limpiar Interfaz".
     */
    @FXML
    public void limpiarInterfaz(ActionEvent actionEvent) {
        controladorBusqueda.limpiarFormulario();
    }
}