package colectivo.servicio;

import colectivo.conexion.Factory;
import colectivo.controlador.CoordinadorApp;
import colectivo.interfaz.Interfaz;

/**
 * Implementación de la interfaz InterfazService que proporciona los servicios relacionados con la interfaz de usuario.
 * Esta clase se encarga de interactuar con la interfaz de usuario, ya sea por consola o por una interfaz gráfica,
 * y de coordinar la comunicación entre la interfaz y el resto de la aplicación a través del CoordinadorApp.
 */
public class InterfazServiceImpl implements InterfazService {

    /**
     * Referencia al objeto Interfaz que se utiliza para interactuar con la interfaz de usuario, ya sea por consola
     * o por una interfaz gráfica.
     */
    private Interfaz interfaz;

    /**
     * Constructor de la clase InterfazServiceImpl que inicializa el objeto Interfaz utilizando la fábrica de conexiones.
     */
    public InterfazServiceImpl() {
        interfaz = (Interfaz) Factory.getInstancia("INTERFAZ", Interfaz.class);
    }

    /**
     * Establece la referencia al CoordinadorApp que se utiliza para coordinar la comunicación entre la interfaz
     * de usuario y el resto de la aplicación.
     * @param coordinadorApp
     */
    @Override
    public void setCoordinadorApp(CoordinadorApp coordinadorApp) {
        interfaz.setCoordinadorApp(coordinadorApp);
    }

    /**
     * Inicia la interfaz de usuario, ya sea por consola o por una interfaz gráfica, y establece la comunicación con el
     * CoordinadorApp para coordinar la interacción entre la interfaz y el resto de la aplicación
     */
    @Override
    public void iniciar() {
        interfaz.iniciar();
    }
}
