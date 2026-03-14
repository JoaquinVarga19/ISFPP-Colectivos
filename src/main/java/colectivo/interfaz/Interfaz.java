package colectivo.interfaz;

import colectivo.controlador.Coordinable;

/**
 * Interfaz de la aplicación.
 * Permite iniciar la aplicación y coordinar con el controlador.
 * Implementa la interfaz Coordinable para permitir la coordinación con el controlador.
 */
public interface Interfaz extends Coordinable  {
        void iniciar();
}
