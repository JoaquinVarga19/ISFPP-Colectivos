package colectivo.interfaz.impl.javafx.controllers;

import javafx.scene.web.WebView;

public class ControladorMapa {

    private WebView mapaWebView;

    public ControladorMapa(WebView mapaWebView) {
        this.mapaWebView = mapaWebView;
        // Cargar el archivo HTML del mapa al WebView
        //this.mapaWebView.getEngine().load(getClass().getResource("/mapa.html").toExternalForm());
    }

}
