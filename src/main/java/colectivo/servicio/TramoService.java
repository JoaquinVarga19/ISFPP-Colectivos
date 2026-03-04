package colectivo.servicio;

import colectivo.modelo.Tramo;

import java.util.Map;

/**
 * Interfaz de servicio para la entidad Tramo.
 */
public interface TramoService {

    /**
     * Inserta un nuevo tramo en el sistema.
     * @param tramo
     */
    void insertar(Tramo tramo);

    /**
     * Actualiza la información de un tramo existente en el sistema.
     * @param tramo
     */
    void actualizar(Tramo tramo);

    /**
     * Elimina un tramo del sistema.
     * @param tramo
     */
    void borrar(Tramo tramo);

    /**
     * Busca y devuelve todos los tramos disponibles en el sistema.
     * @return
     */
    Map<String, Tramo> buscarTodos();
}
