package colectivo.servicio;

import colectivo.controlador.Coordinable;

/**
 * Interfaz que define los métodos que se van a implementar en la clase InterfazServiceImpl, y que se van a usar para
 * interactuar con la interfaz de usuario.
 * Ya sea por consola o por la interfaz grafica
 */
public interface InterfazService extends Coordinable {

    /**
     *
     */
    void iniciar();
}
