package colectivo.dao.postgresql;

import colectivo.conexion.ConexionBD;
import colectivo.dao.ParadaDAO;
import colectivo.modelo.Parada;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Aca se va a implementar la clase ParadaDAOBD que implementa la interfaz ParadaDAO, se van a implementar metodos como
 * insertar, actualizar, borrar y buscar todos para manejar las paradas en la base de datos, utilizando la conexion a la
 * base de datos proporcionada por la clase ConexionBD
 */
public class ParadaDAOBD implements ParadaDAO {

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de ParadaDAOBD
     */
    private static final Logger LOGGER = LogManager.getLogger(ParadaDAOBD.class);

    /**
     * En este metodo se inserta una parada en la base de datos, definimos la consulta SQL con los parametros
     * correspondientes, se hace la conexion a la base de datos utilizando ConexionBD.getConnection(),
     * se prepara la consulta con PreparedStatement y seteamos los parametros de la consulta con los datos de la parada
     * que se quiere insertar.
     * Despues ejecutamos la insercion en la base de datos con ps.executeUpdate() y verificamos si se inserto
     * correctamente, si es asi se registra un mensaje de informacion en el logger.
     * @param parada parada a insertar en la BD.
     * ResultSet no usamos ya que no tenemos que iterar, solo enviar y confirmar (datos).
     * En caso de que la parada ya exista (violacion de clave primaria) se captura la excepcion SQL y se registra un
     * mensaje de error en el logger, indicando que la parada con ese ID ya existe.
     * 23505 es el codigo de error SQL para violacion de clave primaria en PostgreSQL.
     */
    @Override
    public void insertar(Parada parada) {
        String sql = "INSERT INTO \"colectivo_RW\".parada (id_parada, direccion, latitud, longitud) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, parada.getCodigo());
            ps.setString(2, parada.getDireccion());
            ps.setDouble(3, parada.getLatitud());
            ps.setDouble(4, parada.getLongitud());

            //ejecutamos la insercion en la base de datos
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                LOGGER.info("Parada insertada correctamente en la BD: " + parada.getDireccion());
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                LOGGER.error("Error al insertar la parada en la BD: La parada con ID " + parada.getCodigo() + " ya existe.");
            } else {
                throw new RuntimeException(" Error al insertar la parada en la BD: " + e);
            }
        }
    }

    /**
     * Aca actualizamos las paradas desde la BD, definimos la consulta SQL para actualizar los datos de la parada,
     * hacemos la conexion a la base de datos utilizando ConexionBD.getConnection(), preparamos la consulta con
     * PreparedStatement y seteamos los parametros de la consulta con los datos de la parada que se quiere actualizar,
     * incluyendo el ID de la parada para el WHERE.
     * Despues ejecutamos la actualizacion en la base de datos con ps.executeUpdate() y verificamos si se actualizo
     * correctamente, si es asi se registra un mensaje de informacion en el logger.
     * filas es el numero de filas afectadas por la actualizacion, si es 0 significa que no se encontro la parada con
     * el ID especificado, por lo que se registra un mensaje de advertencia en el logger.
     * si devuelve 0 es porque no se encontro el ID especificado, si devuelve 1 es porque se actualizo correctamente
     * en la BD.
     * @param parada a actualizar en la BD
     */
    @Override
    public void actualizar(Parada parada) {
        String sql = "UPDATE \"colectivo_RW\".parada SET direccion = ?, latitud = ?, longitud = ? WHERE id_parada = ?";
        try (Connection con = ConexionBD.getConnection();
              PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, parada.getDireccion());
            ps.setDouble(2, parada.getLatitud());
            ps.setDouble(3, parada.getLongitud());
            ps.setInt(4, parada.getCodigo()); //ID va a lo ultimo por el WHERE

            //ps.executeUpdate();
            int filas = ps.executeUpdate();
            if (filas == 0) {
                LOGGER.warn("No se pudo actualizar la parada. No existe la parada con ID: " + parada.getCodigo());
            } else {
                LOGGER.info("Parada actualizada correctamente en la BD: " + parada.getDireccion());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la parada en la BD: " + e);
        }
    }

    /**
     * Aca se borra una parada de la BD, definimos la consulta SQL para eliminar la parada con el ID especificado,
     * hacemos la conexion a la base de datos utilizando ConexionBD.getConnection(), preparamos la consulta con
     * PreparedStatement y seteamos el parametro de la consulta con el ID de la parada que se quiere eliminar.
     * Despues ejecutamos la eliminacion en la base de datos con ps.executeUpdate() y verificamos si se elimino
     * correctamente, si es asi se registra un mensaje de informacion en el logger.
     * En caso de que la parada este siendo referenciada por otras tablas (tramo y linea) se captura la excepcion SQL
     * y se registra un mensaje de error en el logger, indicando que no se puede eliminar la parada porque esta siendo
     * referenciada por otras tablas.
     * 23503: es el codigo de error SQL para violacion de clave foranea en PostgreSQL, lo que indica que la parada
     * que se intenta eliminar esta siendo referenciada por otras tablas (tramo y linea en este caso).
     * @param parada a borrar de la BD
     */
    @Override
    public void borrar(Parada parada) {
        String sql = "DELETE FROM \"colectivo_RW\".parada WHERE id_parada = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) {

            ps.setInt(1, parada.getCodigo());
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                LOGGER.error("Error al borrar la parada en la BD: No se puede eliminar la parada con ID " +
                        parada.getCodigo() + " porque está siendo referenciada por tramo y linea.");
            }
            throw new RuntimeException("Error al borrar la parada en la BD: " + e);
        }
    }

    /**
     * Metodo para buscar todas las paradas en la base de datos, se devuelve un mapa con el ID de la parada como clave
     * y el objeto Parada como valor.
     * Definimos parametros para la conexion con la BD, despues un PreparedStatement para ejecutar la consulta SQL
     * y un Resultset rs para almacenar el resultado de la consulta.
     * Luego hacemos un mapeo de datos con las columnas de la tabla de paradas en la base de datos, creando un objeto
     * Parada con los datos obtenidos y agregandolo al mapa que se devuelve al final del metodo.
     * Se lanza una excepcion throw new RuntimeException para frenar el proceso en caso de que la BD de un error
     * @return mapa con el ID de la parada como clave y el objeto Parada como valor, con todas las paradas encontradas
     * en la base de datos
     */
    @Override
    public Map<Integer, Parada> buscarTodos() {
        Map<Integer, Parada> mapa = new HashMap<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM \"colectivo_RW\".parada");
             ResultSet rs = ps.executeQuery()){

            //aca es donde hacemos el mapeo de datos
            while (rs.next()) {
                int id = rs.getInt("id_parada");
                String direccion = rs.getString("direccion");
                double latitud = rs.getDouble("latitud");
                double longitud = rs.getDouble("longitud");

                Parada parada = new Parada(id, direccion, latitud, longitud);
                mapa.put(id, parada);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al buscar todas las paradas en la BD", ex);
        }
        return mapa;
    }
}