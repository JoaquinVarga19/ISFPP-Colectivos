package colectivo.dao;

import colectivo.modelo.Parada;

import java.util.Map;

/**
 * Interfaz DAO para la entidad Parada con operaciones CRUD y busqueda de todas las paradas disponibles
 */
public interface ParadaDAO {

    /**
     * Inserta una nueva parada en el sistema
     */
    void insertar(Parada parada);

    /**
     * Actualiza una parada existente
     */
    void actualizar(Parada parada);

    /**
     * Borra una parada del sistema
     */
    void borrar(Parada parada);

    /**
     * Busca y devuelve todas las paradas disponibles en el sistema
     */
    Map<Integer, Parada> buscarTodos();
}
