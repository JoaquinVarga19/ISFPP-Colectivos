package colectivo.interfaz.impl.javafx.controllers;

import javafx.application.Platform;
import javafx.scene.web.WebView;

/**
 * Controlador para manejar la lógica relacionada con el mapa en la interfaz JavaFX.
 * Este controlador se encargará de cargar el mapa, dibujar rutas y limpiar rutas según las acciones del usuario.
 * Cargamos el mapa desde OpenStreetMap utilizando un archivo HTML que se encuentra en los recursos del proyecto.
 * */
public class ControladorMapa {

    /**
     * Conexión al WebView que muestra el mapa. Este WebView se define en el archivo FXML y se inyecta en este controlador.
     */
    private WebView mapaWebView;

    /**
     * Constructor del controlador del mapa. Recibe el WebView que se usará para mostrar el mapa.
     * @param mapaWebView
     */
    public ControladorMapa(WebView mapaWebView) {
        this.mapaWebView = mapaWebView;
        // Cargar el archivo HTML del mapa al WebView
        //this.mapaWebView.getEngine().load(getClass().getResource("resources/mapa.html").toExternalForm());
        try {
            String urlMapa = getClass().getResource("/mapa.html").toExternalForm();
            this.mapaWebView.getEngine().load(urlMapa);
            System.out.println("Cargando mapa desde: " + urlMapa);
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo mapa.html: " + e.getMessage());
        }
    }

    /**
     * Llamamos a la funcion JavaScript para limpiar las rutas dibujadas en el mapa. Esta función debe estar definida
     * en el archivo HTML del mapa.
     */
    public void limpiarRutas() {
        Platform.runLater(() -> {
            try {
                // Llamamos a la función JavaScript "limpiarRutas" que debe estar definida en el mapa.html
                this.mapaWebView.getEngine().executeScript("limpiarMapaDesdeJava()");
            } catch (Exception e) {
                System.err.println("Error al ejecutar la función JavaScript limpiarRutas: " + e.getMessage());
            }
        });
    }

    /**
     * Llamamos a la función JavaScript para dibujar las rutas en el mapa.
     * @param datosRuta Los datos de la ruta que se deben dibujar, en el formato que tu función JavaScript espera.
     */
    public void dibujarRutas(String datosRuta) {
        Platform.runLater(() -> {
            try {
                // Llamamos a la función JavaScript "dibujarRutas" que debe estar definida en el mapa.html
                // Pasamos los datos de la ruta como argumento a la función JavaScript
                this.mapaWebView.getEngine().executeScript("dibujarRutaDesdeJava('" + datosRuta + "')");
            } catch (Exception e) {
                System.err.println("Error al ejecutar la función JavaScript dibujarRutas: " + e.getMessage());
            }
        });
    }
}