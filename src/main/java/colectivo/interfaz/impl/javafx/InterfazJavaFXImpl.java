package colectivo.interfaz.impl.javafx;

import colectivo.controlador.Coordinable;
import colectivo.controlador.CoordinadorApp;
import colectivo.interfaz.Interfaz;
import colectivo.interfaz.impl.javafx.controllers.ControladorPantallaPrincipal;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InterfazJavaFXImpl extends Application implements Interfaz, Coordinable {
    // Variable estática para guardar el coordinador y usarlo en JavaFX
    private static CoordinadorApp coordinadorApp;

    // --- MÉTODOS DE TU ARQUITECTURA ---

    @Override
    public void setCoordinadorApp(CoordinadorApp coordinador) {
        // Guardamos el coordinador en la estática antes de lanzar la ventana
        InterfazJavaFXImpl.coordinadorApp = coordinador;
    }

    @Override
    public void iniciar() {
        // Este método es el que llama tu Coordinador. Aquí "encendemos" JavaFX.
        System.out.println("Iniciando entorno gráfico JavaFX...");
        Application.launch(InterfazJavaFXImpl.class);
    }

    // --- MÉTODOS PROPIOS DE JAVAFX ---

    @Override
    public void start(Stage escenarioPrincipal) {
        try {
            // 1. Cargamos el archivo visual (el Local)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/pantalla.fxml"));
            Scene escena = new Scene(fxmlLoader.load());

            // --- ESTA ES LA MODIFICACIÓN CLAVE ---
            // 2. Le pedimos a JavaFX que nos dé al "Gerente" que acaba de crear
            ControladorPantallaPrincipal controlador = fxmlLoader.getController();

            // 3. Le inyectamos la "Cocina" (tu coordinador)
            controlador.setCoordinadorApp(coordinadorApp); // Asegurate de usar tu variable acá
            // -------------------------------------

            // 4. Mostramos la ventana
            escenarioPrincipal.setTitle("Sistema de Colectivos");
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.show();

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la interfaz gráfica: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
