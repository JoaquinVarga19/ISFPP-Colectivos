package colectivo.dao.postgresql;

import colectivo.conexion.ConexionBD;
import colectivo.conexion.Factory;
import colectivo.dao.ParadaDAO;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Aca se va a implementar la clase TramoDAOBD que implementa la interfaz TramoDAO, se van a implementar metodos como
 * insertar, actualizar, borrar y buscar todos para manejar los tramos en la base de datos, utilizando la conexion a la
 * base de datos proporcionada por la clase ConexionBD.
 */
public class TramoDAOBD implements TramoDAO {

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de TramoDAOBD
     */
    private static final Logger LOGGER = LogManager.getLogger(TramoDAOBD.class);

    /**
     * Mapa que almacena las paradas cargadas con su codigo como clave y al objeto paradas
     */
    private final Map<Integer, Parada> paradasCargadas;

    /**
     *  Mapa de tramos que almacena el tramo con su codigo como clave y al objeto tramo como valor.
     */
    private Map<String, Tramo> tramosMap;

    /**
     * Indica si se actualizo la base de datos al realizar el CRUD.
     */
    private boolean actualizar = true;

    /**
     * Constructor de la clase TramoDAOBD que recibe un mapa de paradas cargadas para su uso en las operaciones de tramos.
     */
    public TramoDAOBD() {
        this.paradasCargadas = cargarParadas();
    }

    /**
     * Aca se implementa el metodo que inserta un tramo en la base de datos, se define la consulta SQL con los parametros
     * correspondientes, se hace la conexion a la base de datos utilizando ConexionBD.getConnection(), se prepara la
     * consulta con PreparedStatement y seteamos los parametros de la consulta con los datos del tramo que se quiere
     * insertar.
     *
     * @param tramo tramo a insertar en la BD.
     */
    @Override
    public void insertar(Tramo tramo) {
        String sql = "INSERT INTO \"colectivo_RW\".tramo (id_origen, id_destino, tiempo, tipo) VALUES (?, ?, ?, ?)";
        Connection con = ConexionBD.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tramo.getInicio().getCodigo());
            ps.setInt(2, tramo.getFin().getCodigo());
            ps.setInt(3, tramo.getTiempo());
            ps.setInt(4, tramo.getTipo());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                LOGGER.info("Tramo insertado correctamente en la BD: " + tramo.getInicio().getDireccion() + " a " +
                        tramo.getFin().getDireccion());
                actualizar = true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(" Error al insertar el tramo en la BD: " + e);
        }
    }

    /**
     * Aca se actualizan los tramos en la base de datos, se define la consulta SQL para actualizar el tiempo del tramo,
     * se hace la conexion a la base de datos utilizando ConexionBD.getConnection(), se prepara la consulta con
     * PreparedStatement y seteamos los parametros de la consulta con los datos del tramo que se quiere actualizar,
     * se ejecuta la actualizacion en la base de datos con ps.executeUpdate() y se verifica si se actualizo
     * correctamente, si es asi se registra un mensaje de informacion en el logger, indicando que el tramo se actualizo
     * correctamente, si no se encontro el tramo para actualizar se registra una advertencia en el logger.
     *
     * @param tramo a actualizar en la BD.
     */
    @Override
    public void actualizar(Tramo tramo) {
        String sql = "UPDATE \"colectivo_RW\".tramo SET tiempo = ? WHERE id_origen = ? AND id_destino = ? AND tipo = ?";
        Connection con = ConexionBD.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tramo.getTiempo());
            ps.setInt(2, tramo.getInicio().getCodigo());
            ps.setInt(3, tramo.getFin().getCodigo());
            ps.setInt(4, tramo.getTipo());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                LOGGER.info("Tramo actualizado correctamente en la BD: " + tramo.getInicio().getDireccion() + " a " +
                        tramo.getFin().getDireccion());
                actualizar = true;
            } else {
                LOGGER.warn("No se encontró el tramo para actualizar en la BD: " + tramo.getInicio().getDireccion() +
                        " a " + tramo.getFin().getDireccion());
            }

        } catch (SQLException e) {
            throw new RuntimeException(" Error al actualizar el tramo en la BD: " + e);
        }
    }

    /**
     * Aca se implementa el metodo que borra un tramo de la base de datos, se define la consulta SQL para eliminar el tramo
     * se hace la conexion a la base de datos utilizando ConexionBD.getConnection(), se prepara la consulta con
     * PreparedStatement y seteamos los parametros de la consulta con los datos del tramo que se quiere borrar, se
     * ejecuta la eliminacion en la base de datos con ps.executeUpdate() y se verifica si se borro correctamente, si es
     * asi se registra un mensaje de informacion en el logger, indicando que el tramo se borro correctamente, si no se
     * encontro el tramo para borrar se registra una advertencia en el logger.
     *
     * @param tramo a borrar de la BD.
     */
    @Override
    public void borrar(Tramo tramo) {
        String sql = "DELETE FROM \"colectivo_RW\".tramo WHERE id_origen = ? AND id_destino = ? AND tipo = ?";
        Connection con = ConexionBD.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tramo.getInicio().getCodigo());
            ps.setInt(2, tramo.getFin().getCodigo());
            ps.setInt(3, tramo.getTipo());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                LOGGER.info("Tramo borrado correctamente en la BD: " + tramo.getInicio().getDireccion() + " a " + tramo.getFin().getDireccion());
                actualizar = true;
            } else {
                LOGGER.warn("No se encontró el tramo para borrar en la BD: " + tramo.getInicio().getDireccion() + " a " + tramo.getFin().getDireccion());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return mapa con los tramos encontrados en la BD.
     */
    @Override
    public Map<String, Tramo> buscarTodos() {
        if (actualizar || tramosMap == null) {
            this.tramosMap = leerDesdeBD();
            this.actualizar = true;
        }
        return this.tramosMap;
    }

    /**
     * Metodo para cargar las paradas desde la base de datos, se obtiene una instancia de ParadaDAO desde la Factory
     * y se llama al metodo buscarTodos() para obtener un mapa con todas las paradas, si ocurre un error al obtener la
     * instancia de ParadaDAO o al buscar las paradas se captura la excepcion y se registra un mensaje de error en el
     * logger, devolviendo un mapa vacio en caso de error.
     * @return mapa con el ID de la parada como clave y el objeto Parada como valor, con todas las paradas encontradas en la base de datos
     */
    private Map<Integer, Parada> cargarParadas() {
        try {
            ParadaDAO paradaDAO = Factory.getInstancia("PARADA", ParadaDAO.class);
            return paradaDAO.buscarTodos();
        } catch (Exception e) {
            LOGGER.fatal("Error al obtener ParadaDAO desde la Factory en TramoDAO: ", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Metodo privado para leer todas los tramos de la base de datos, se devuelve un mapa con el codigo del tramo como clave
     * y el objeto Tramo como valor, se define la consulta SQL para seleccionar los tramos de la tabla
     * "colectivo_RW".tramo, se hace la conexion a la base de datos utilizando ConexionBD.getConnection(),
     * se prepara la consulta con PreparedStatement y se ejecuta la consulta con ps.executeQuery() para obtener un
     * ResultSet con los resultados de la consulta, se itera sobre el ResultSet para crear los objetos Tramo
     * correspondientes a cada fila de resultados, se obtiene el ID de origen y destino de cada tramo, se buscan las
     * paradas correspondientes en el mapa de paradas cargadas, se crea un objeto Tramo con los datos obtenidos y se
     * agrega al mapa de tramos con el codigo del tramo como clave, si no se encuentran las paradas de origen o destino
     * para un tramo se registra una advertencia en el logger indicando que no se pudo crear el tramo debido a que no se
     * encontraron las paradas de origen o destino para ese tramo, finalmente se devuelve el mapa de tramos con los
     * tramos encontrados en la base de datos.
     * @return mapa con el codigo del tramo como clave y le  objeto Tramo como valor.
     */
    private Map<String, Tramo> leerDesdeBD() {
        Map<String, Tramo> mapa = new HashMap<>();
        String sql = "SELECT id_origen, id_destino, tiempo, tipo FROM \"colectivo_RW\".tramo";
        Connection con = ConexionBD.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idOrigen = rs.getInt("id_origen");
                int idDestino = rs.getInt("id_destino");
                int duracion = rs.getInt("tiempo");
                int tipo = rs.getInt("tipo");

                Parada origen = paradasCargadas.get(idOrigen);
                Parada destino = paradasCargadas.get(idDestino);

                if (origen != null && destino != null) {
                    Tramo tramo = new Tramo(origen, destino, rs.getInt("tiempo"), rs.getInt("tipo"));
                    String codigoTramo = idOrigen + "-" + idDestino + "-" + tipo;
                    mapa.put(codigoTramo, tramo);
                } else {
                    LOGGER.warn("No se pudo crear el tramo debido a que no se encontraron las paradas de origen o " +
                            "destino para el tramo con ID origen: " + idOrigen + " y ID destino: " + idDestino);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar todos los tramos en la BD", e);
        }
        return mapa;
    }
}