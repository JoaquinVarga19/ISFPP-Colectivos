package colectivo.servicio;

import colectivo.modelo.Linea;

import java.util.Map;

/**
 * Interfaz que define los servicios relacionados con las líneas de colectivo.
 */
public interface LineaService {

    /**
     * Inserta una nueva línea de colectivo en el sistema.
     * @param linea
     */
    void insertar(Linea linea);

    /**
     * Actualiza la información de una línea de colectivo existente en el sistema.
     * @param linea
     */
    void actualizar(Linea linea);

    /**
     * Elimina una línea de colectivo del sistema.
     * @param linea
     */
    void borrar(Linea linea);

    /**
     * Busca y devuelve todas las líneas de colectivo disponibles en el sistema.
     * @return
     */
    Map<String, Linea> buscarTodos();
}
