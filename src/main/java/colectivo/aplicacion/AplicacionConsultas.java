package colectivo.aplicacion;

import colectivo.controlador.CoordinadorApp;

public class AplicacionConsultas {
    public static void main(String[] args) {
        // 1. Instanciamos el Coordinador (el "cerebro" del sistema)

        CoordinadorApp coordinador = new CoordinadorApp();
        System.out.println("DEBUG: Iniciando la aplicación de consultas...");
        // 2. Iniciamos la aplicación
        // Este método cargará la configuración, servicios, datos y finalmente la interfaz
        coordinador.inicializarAplicacion();
    }
}