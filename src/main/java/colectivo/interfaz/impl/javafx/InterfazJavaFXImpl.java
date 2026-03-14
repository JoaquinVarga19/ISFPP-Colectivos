package colectivo.interfaz.impl.javafx;

import colectivo.controlador.Coordinable;
import colectivo.controlador.CoordinadorApp;
import colectivo.interfaz.Interfaz;
import colectivo.interfaz.impl.javafx.controllers.ControladorPantallaPrincipal;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Esta clase es la implementación de tu interfaz gráfica usando JavaFX.
 * Aquí es donde se inicia JavaFX y se carga tu archivo visual (FXML).
 * Además, es donde se inyecta el coordinador (la "Cocina") en el controlador de la pantalla.
 */
public class InterfazJavaFXImpl extends Application implements Interfaz, Coordinable {

    /**
     * Variable estática para guardar el coordinador y usarlo en JavaFX.
     */
    private static CoordinadorApp coordinadorApp;

    /**
     * Método para inyectar el coordinador desde la aplicación principal.
     * @param coordinador El coordinador que se inyectará en la interfaz gráfica.
     */
    @Override
    public void setCoordinadorApp(CoordinadorApp coordinador) {
        InterfazJavaFXImpl.coordinadorApp = coordinador;
    }

    /**
     * Método para iniciar la interfaz gráfica. Aquí se lanza la aplicación JavaFX.
     */
    @Override
    public void iniciar() {
        System.out.println("Iniciando entorno gráfico JavaFX...");
        Application.launch(InterfazJavaFXImpl.class);
    }

    /**
     * Método que se llama al iniciar la aplicación JavaFX. Aquí se carga el archivo FXML y se muestra la ventana principal.
     * @param escenarioPrincipal El escenario principal de JavaFX donde se mostrará la interfaz gráfica.
     */
    @Override
    public void start(Stage escenarioPrincipal) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/pantalla.fxml"));
            Scene escena = new Scene(fxmlLoader.load());

            ControladorPantallaPrincipal controlador = fxmlLoader.getController();

            controlador.setCoordinadorApp(coordinadorApp);

            escenarioPrincipal.setTitle("Sistema de Colectivos");
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.show();

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la interfaz gráfica: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
