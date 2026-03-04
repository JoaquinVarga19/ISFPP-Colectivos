package colectivo.servicio;

import colectivo.modelo.Parada;

import java.util.Map;

/**
 * Interfaz de servicio para la entidad Parada.
 */
public interface ParadaService {

    /**
     * Inserta una nueva parada en el sistema.
     * @param parada
     */
    void insertar(Parada parada);

    /**
     * Actualiza la información de una parada existente en el sistema.
     * @param parada
     */
    void actualizar(Parada parada);

    /**
     * Elimina una parada del sistema.
     * @param parada
     */
    void borrar(Parada parada);

    /**
     * Busca y devuelve todas las paradas disponibles en el sistema.
     * @return
     */
    Map<Integer, Parada> buscarTodos();

}
