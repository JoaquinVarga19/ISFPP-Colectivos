package colectivo.dao;

import colectivo.modelo.Tramo;

import java.util.Map;

/**
 * Interfaz DAO para la entidad Tramo con operaciones CRUD y busqueda de todos los tramos disponibles
 */
public interface TramoDAO {

    /**
     * Inserta un nuevo tramo en el sistema
     */
    void insertar(Tramo tramo);

    /*
     * Actualiza un tramo existente
     */
    void actualizar(Tramo tramo);

    /*
     * Borra un tramo del sistema
     */
    void borrar(Tramo tramo);

    /**
     * Busca y devuelve todos los tramos disponibles en el sistema
     */
    Map<String, Tramo> buscarTodos();
}
