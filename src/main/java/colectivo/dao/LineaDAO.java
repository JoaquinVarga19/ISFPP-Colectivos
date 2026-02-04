package colectivo.dao;

import colectivo.modelo.Linea;

import java.util.Map;

/**
 * Interfaz DAO para la entidad Linea con operaciones CRUD y busqueda de todas las lineas disponibles
 */
public interface LineaDAO {

    /**
     * Inserta una nueva linea en el sistema
     */
    void insertar(Linea linea);

    /**
     * Actualiza una linea existente
     */
    void actualizar(Linea linea);

    /**
     * Borra una linea del sistema
     */
    void borrar(Linea linea);

    /**
     * Busca y devuelve todas las lineas disponibles en el sistema
     */
    Map<String, Linea> buscarTodos();
}
