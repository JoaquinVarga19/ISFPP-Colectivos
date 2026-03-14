package colectivo.aplicacion;

import colectivo.controlador.CoordinadorApp;

/**
 * Clase principal de la aplicación de consultas.
 * Aquí se inicia el programa y se coordina la inicialización de todos los componentes.
 * coordinador va a ser el cerebro del sistema.
 */
public class AplicacionConsultas {
    public static void main(String[] args) {
        CoordinadorApp coordinador = new CoordinadorApp();
        System.out.println("DEBUG: Iniciando la aplicación de consultas...");
        coordinador.inicializarAplicacion();
    }
}