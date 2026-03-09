package colectivo.controlador;

/**
 * Interfaz que define el contrato para cualquier clase que quiera ser coordinada por el CoordinadorApp.
 */
public interface Coordinable {

    /**
     * Este metodo se utiliza para establecer una referencia al CoordinadorApp en la clase que implementa esta interfaz,
     * lo que permite a esa clase comunicarse con el CoordinadorApp y acceder a sus métodos y funcionalidades.
     * Esto es esencial para la coordinación de la aplicación, ya que permite que las diferentes partes de la aplicación
     * trabajen juntas de manera eficiente.
     */
    void setCoordinadorApp(CoordinadorApp coordinadorApp);
}
