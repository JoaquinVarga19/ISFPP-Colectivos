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
     * @param mapaWebView El WebView que se usará para mostrar el mapa. Este WebView debe estar definido en el archivo
     * FXML y se inyectará en este controlador.
     */
    public ControladorMapa(WebView mapaWebView) {
        this.mapaWebView = mapaWebView;
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
     * en el archivo HTML del mapa. Se llama a la funcion JavaScript "limpiarMapaDesdeJava" que se encarga de limpiar el
     * mapa de cualquier ruta dibujada previamente.
     */
    public void limpiarRutas() {
        Platform.runLater(() -> {
            try {
                this.mapaWebView.getEngine().executeScript("limpiarMapaDesdeJava()");
            } catch (Exception e) {
                System.err.println("Error al ejecutar la función JavaScript limpiarRutas: " + e.getMessage());
            }
        });
    }

    /**
     * Llamamos a la función JavaScript para dibujar las rutas en el mapa.
     * Llamamos a funciones javascript que deben estar definidas en el archivo HTML del mapa, pasando los datos de la
     * ruta como argumento.
     * @param datosRuta Los datos de la ruta que se deben dibujar, en el formato que tu función JavaScript espera.
     */
    public void dibujarRutas(String datosRuta) {
        Platform.runLater(() -> {
            try {
                this.mapaWebView.getEngine().executeScript("dibujarRutaDesdeJava('" + datosRuta + "')");
            } catch (Exception e) {
                System.err.println("Error al ejecutar la función JavaScript dibujarRutas: " + e.getMessage());
            }
        });
    }
}