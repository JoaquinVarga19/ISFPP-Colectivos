package colectivo.servicio;

import colectivo.conexion.Factory;
import colectivo.dao.ParadaDAO;
import colectivo.modelo.Parada;

import java.util.Map;

/**
 * Implementación de la interfaz ParadaService que proporciona los servicios relacionados con las paradas de colectivo.
 */
public class ParadaServiceImpl implements ParadaService {

    /**
     * Referencia al objeto ParadaDAO que se utiliza para realizar operaciones de acceso a datos relacionadas con las
     * paradas de colectivo.
     */
    private ParadaDAO paradaDAO;

    /**
     * Constructor de la clase ParadaServiceImpl que inicializa el objeto ParadaDAO utilizando la fábrica de conexiones.
     */
    public ParadaServiceImpl() {
        paradaDAO = (ParadaDAO) Factory.getInstancia("PARADA", ParadaDAO.class);
    }

    /**
     * Inserta una nueva parada en el sistema utilizando el método insertar del objeto ParadaDAO.
     * @param parada
     */
    @Override
    public void insertar(Parada parada) {
        paradaDAO.insertar(parada);
    }

    /**
     * Actualiza la información de una parada existente en el sistema utilizando el método actualizar del objeto
     * ParadaDAO.
     * @param parada
     */
    @Override
    public void actualizar(Parada parada) {
        paradaDAO.actualizar(parada);
    }

    /**
     * Elimina una parada del sistema utilizando el método borrar del objeto ParadaDAO.
     * @param parada
     */
    @Override
    public void borrar(Parada parada) {
        paradaDAO.borrar(parada);
    }

    /**
     * Busca y devuelve todas las paradas disponibles en el sistema utilizando el método buscarTodos del objeto ParadaDAO.
     * @return
     */
    @Override
    public Map<Integer, Parada> buscarTodos() {
        return paradaDAO.buscarTodos();
    }
}
