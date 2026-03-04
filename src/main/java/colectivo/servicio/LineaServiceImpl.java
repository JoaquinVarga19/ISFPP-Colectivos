package colectivo.servicio;

import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.modelo.Linea;

import java.util.Map;

/**
 * Implementación de la interfaz LineaService que proporciona los servicios relacionados con las líneas de colectivo.
 */
public class LineaServiceImpl implements LineaService {

    /**
     * Referencia al objeto LineaDAO que se utiliza para realizar operaciones de acceso a datos relacionadas con las
     * líneas de colectivo.
     */
    private LineaDAO lineaDAO;

    /**
     * Constructor de la clase LineaServiceImpl que inicializa el objeto LineaDAO utilizando la fábrica de conexiones.
     */
    public LineaServiceImpl() {
        lineaDAO = (LineaDAO) Factory.getInstancia("LINEA", LineaDAO.class);
    }

    /**
     * Inserta una nueva línea de colectivo en el sistema utilizando el método insertar del objeto LineaDAO.
     * @param linea
     */
    @Override
    public void insertar(Linea linea) {
        lineaDAO.insertar(linea);
    }

    /**
     * Actualiza la información de una línea de colectivo existente en el sistema utilizando el método actualizar del
     * objeto LineaDAO.
     * @param linea
     */
    @Override
    public void actualizar(Linea linea) {
        lineaDAO.actualizar(linea);
    }

    /**
     * Elimina una línea de colectivo del sistema utilizando el método borrar del objeto LineaDAO.
     * @param linea
     */
    @Override
    public void borrar(Linea linea) {
        lineaDAO.borrar(linea);
    }

    /**
     * Busca y devuelve todas las líneas de colectivo disponibles en el sistema utilizando el método buscarTodos del
     * @return
     */
    @Override
    public Map<String, Linea> buscarTodos() {
        return lineaDAO.buscarTodos();
    }
}