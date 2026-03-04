package colectivo.servicio;

import colectivo.conexion.Factory;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Tramo;

import java.util.Map;

/**
 * Implementación de la interfaz TramoService que proporciona los servicios relacionados con los tramos de colectivo.
 */
public class TramoServiceImpl implements TramoService {

    /**
     * Referencia al objeto TramoDAO que se utiliza para realizar operaciones de acceso a datos relacionadas con los
     * tramos de colectivo.
     */
    private TramoDAO tramoDAO;

    /**
     * Constructor de la clase TramoServiceImpl que inicializa el objeto TramoDAO utilizando la fábrica de conexiones.
     */
    public TramoServiceImpl() {
        tramoDAO = (TramoDAO) Factory.getInstancia("TRAMO", TramoDAO.class);
    }

    /**
     * Inserta un nuevo tramo en el sistema utilizando el método insertar del objeto TramoDAO.
     * @param tramo
     */
    @Override
    public void insertar(Tramo tramo) {
        tramoDAO.insertar(tramo);
    }

    /**
     * Actualiza la información de un tramo existente en el sistema utilizando el método actualizar del objeto TramoDAO.
     * @param tramo
     */
    @Override
    public void actualizar(Tramo tramo) {
        tramoDAO.actualizar(tramo);
    }

    /**
     * Elimina un tramo del sistema utilizando el método borrar del objeto TramoDAO.
     * @param tramo
     */
    @Override
    public void borrar(Tramo tramo) {
        tramoDAO.borrar(tramo);
    }

    /**
     * Busca y devuelve todos los tramos disponibles en el sistema utilizando el método buscarTodos del objeto TramoDAO.
     * @return
     */
    @Override
    public Map<String, Tramo> buscarTodos() {
        return tramoDAO.buscarTodos();
    }
}
