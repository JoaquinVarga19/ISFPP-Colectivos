package colectivo.dao.postgresql;

import colectivo.conexion.ConexionBD;
import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.dao.ParadaDAO;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.Time;


/**
 * Aca se va a implementar la clase LineaDAOBD que implementa la interfaz LineaDAO, se van a implementar metodos como
 * insertar, actualizar, borrar y buscar todos para manejar las lineas en la base de datos, utilizando la conexion a la
 * base de datos proporcionada por la clase ConexionBD. LineaDAOBD tambien depende de las paradas para cargar las lineas
 * correctamente, por lo que se carga un mapa de paradas al instanciar la clase, utilizando el ParadaDAO para obtener
 * todas las paradas cargadas y almacenarlas en un mapa con su codigo como clave y al objeto parada como valor, para
 * luego usar ese mapa en las operaciones de lineas que dependen de las paradas.
 */
public class LineaDAOBD implements LineaDAO {

    /**
     * Logger para registrar eventos y errores relacionados con las operaciones de LineaDAOBD
     */
    private static final Logger LOGGER = LogManager.getLogger(LineaDAOBD.class);

    /**
     * Mapa que almacena las paradas cargadas con su codigo como clave y al objeto paradas, se utiliza para las
     * operaciones de lineas que dependen de las paradas.
     */
    private final Map<Integer, Parada> paradasCargadas;

    /**
     * Mapa de lineas que almacena la linea como clave el codigo de la linea y el objeto Linea como valor.
     */
    private Map<String, Linea> lineasMap;

    /**
     * Bandera que indica si se actualizo la base de datos al realizar el CRUD.
     */
    private boolean actualizar = true;

    /**
     * Constructor de la clase LineaDAOBD que recibe un mapa de paradas cargadas para su uso en las operaciones de
     * lineas.
     */
    public LineaDAOBD() {
        this.paradasCargadas = cargarParadas();
    }

    /**
     * Aca se implementa el metodo que inserta una linea en la base de datos, se define la consulta SQL con los parametros
     * correspondientes, se hace la conexion a la base de datos utilizando ConexionBD.getConnection(), se prepara la
     * consulta con PreparedStatement y seteamos los parametros de la consulta con los datos de la linea que se quiere
     * insertar.
     * Se utiliza una transaccion para asegurar que todas las inserciones relacionadas con la linea (en linea,
     * linea_parada y linea_frecuencia) se realicen correctamente, si alguna falla se hace un rollback para mantener la
     * integridad de la base de datos.
     * @param linea linea a insertar en la BD.
     */
    @Override
    public void insertar(Linea linea) {
        Connection con = null;
        con = ConexionBD.getConnection();
        try {
            con.setAutoCommit(false); // Iniciamos la transacción, nada se guardará hasta que confirmemos que t0d0 salió bien
            String sqlLinea = "INSERT INTO \"colectivo_RW\".linea (codigo, nombre) VALUES (?, ?)";
            try (PreparedStatement psl = con.prepareStatement(sqlLinea)) {
                psl.setString(1, linea.getCodigo());
                psl.setString(2, linea.getNombre());
                psl.executeUpdate();
            }

            String sqlPl = "INSERT INTO \"colectivo_RW\".linea_parada (codigo_linea, id_parada, orden) VALUES (?, ?, ?)";
            try (PreparedStatement psPl = con.prepareStatement(sqlPl)) {
                int orden = 1; //Empezamos de orden 1 para la primera parada, y vamos incrementando para cada parada siguiente
                for (Parada parada : linea.getParadas()) {
                    psPl.setString(1, linea.getCodigo());
                    psPl.setInt(2, parada.getCodigo());
                    psPl.setInt(3, orden++);
                    psPl.executeUpdate();
                }
            }

            String sqlFrec = "INSERT INTO \"colectivo_RW\".linea_frecuencia (codigo_linea, dia_semana, hora) VALUES (?, ?, ?)";
            try (PreparedStatement psFrec = con.prepareStatement(sqlFrec)) {
                for (Linea.Frecuencia frec : linea.getFrecuencias()){
                    psFrec.setString(1, linea.getCodigo());
                    psFrec.setInt(2, frec.getDiaSemana());
                    psFrec.setTime(3, Time.valueOf(frec.getHora()));
                    psFrec.executeUpdate();
                }
            }

            //Guardamos la transaccion si todo salio bien.
            con.commit();
            this.actualizar = true;
            LOGGER.info("Linea " + linea.getCodigo() + " insertada correctamente en la BD: " + linea.getNombre());

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Error al hacer rollback de la transacción en LineaDAOBD...", ex);
                }
            }
            throw new RuntimeException("Error al insertar la línea completa", e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restauramos el modo de autocommit para futuras operaciones
                    //con.close();
                } catch (SQLException e) {
                    LOGGER.error("Error al cerrar la conexión en LineaDAOBD...", e);
                }
            }
        }
    }

    /**
     * Aca se implementa el metodo que actualiza una linea en la base de datos, se define la consulta SQL con los parametros
     * correspondientes, se hace la conexion a la base de datos utilizando ConexionBD.getConnection(), se prepara la
     * consulta con PreparedStatement y seteamos los parametros de la consulta con los datos de la linea que se quiere
     * actualizar, incluyendo el codigo de la linea para el WHERE.
     * @param linea a actualizar en la BD.
     */
    @Override
    public void actualizar(Linea linea) {
        Connection con = null;
        con = ConexionBD.getConnection();
        try {
            con.setAutoCommit(false); // Iniciamos la transacción

            String sqlLinea = "UPDATE \"colectivo_RW\".linea SET nombre = ? WHERE codigo = ?";
            try (PreparedStatement psl = con.prepareStatement(sqlLinea)) {
                psl.setString(1, linea.getNombre());
                psl.setString(2, linea.getCodigo());
                psl.executeUpdate();
            }

            //Se borran las paradas y frecuencias anteriores para insertar las nuevas, ya que no se especifica una
            // logica de actualizacion
            String sqlPlDelete = "DELETE FROM \"colectivo_RW\".linea_parada WHERE codigo_linea = ?";
            String sqlFrecDelete = "DELETE FROM \"colectivo_RW\".linea_frecuencia WHERE codigo_linea = ?";
            try (PreparedStatement psPlDelete = con.prepareStatement(sqlPlDelete);
                 PreparedStatement psFrecDelete = con.prepareStatement(sqlFrecDelete)) {
                psPlDelete.setString(1, linea.getCodigo());
                psPlDelete.executeUpdate();
                psFrecDelete.setString(1, linea.getCodigo());
                psFrecDelete.executeUpdate();
            }

            //Insertamos paradas actuales (linea_parada)
            String sqlPlInsert = "INSERT INTO \"colectivo_RW\".linea_parada (codigo_linea, id_parada, orden) VALUES (?, ?, ?)";
            try (PreparedStatement psPlInsert = con.prepareStatement(sqlPlInsert)) {
                int orden = 1; //Empezamos de orden 1 para la primera parada, y vamos incrementando para cada parada siguiente
                for (Parada parada : linea.getParadas()) {
                    psPlInsert.setString(1, linea.getCodigo());
                    psPlInsert.setInt(2, parada.getCodigo());
                    psPlInsert.setInt(3, orden++);
                    psPlInsert.executeUpdate();
                }
            }

            //insertamos frecuencias actuales (linea_frecuencia)
            String sqlFrecInsert = "INSERT INTO \"colectivo_RW\".linea_frecuencia (codigo_linea, dia_semana, hora) VALUES (?, ?, ?)";
            try (PreparedStatement psFrecInsert = con.prepareStatement(sqlFrecInsert)) {
                for (Linea.Frecuencia frec : linea.getFrecuencias()) {
                    psFrecInsert.setString(1, linea.getCodigo() );
                    psFrecInsert.setInt(2, frec.getDiaSemana());
                    psFrecInsert.setTime(3, Time.valueOf(frec.getHora()));
                    psFrecInsert.executeUpdate();
                }
            }

            con.commit(); // Guardamos la transacción si todo salió bien
            this.actualizar = true;
            LOGGER.info("Linea " + linea.getCodigo() + " actualizada correctamente en la BD: " + linea.getNombre());

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Error al hacer rollback de la transacción en LineaDAOBD...");
                }
            }
            throw new RuntimeException("Error al actualizar la línea completa", e);
        } finally {
             if (con != null) {
                 try {
                     con.setAutoCommit(true); // Restauramos el modo de autocommit para futuras operaciones
                     //con.close();
                 } catch (SQLException e) {
                     LOGGER.error("Error al cerrar la conexión en LineaDAOBD...", e);
                 }
             }
        }
    }

    /**
     * Aca se implementa el metodo que borra una linea de la base de datos, se define la consulta SQL con los parametros
     * correspondientes, se hace la conexion a la base de datos utilizando ConexionBD.getConnection(), se prepara la
     * consulta con PreparedStatement y seteamos los parametros de la consulta con los datos de la linea que se quiere
     * borrar, incluyendo el codigo de la linea para el WHERE.
     * @param linea a borrar de la BD.
     */
    @Override
    public void borrar(Linea linea) {
        Connection con = null;
        con = ConexionBD.getConnection();
        try {
            con.setAutoCommit(false);

            //Borramos dependencias de las tablas hijas (linea_parada y linea_frecuencia)
            String sqlDelPl = "DELETE FROM \"colectivo_RW\".linea_parada WHERE codigo_linea = ?";
            String sqlDelFrec = "DELETE FROM \"colectivo_RW\".linea_frecuencia WHERE codigo_linea = ?";
            try (PreparedStatement psDelPl = con.prepareStatement(sqlDelPl);
                 PreparedStatement psDelFrec = con.prepareStatement(sqlDelFrec);) {
                psDelPl.setString(1, linea.getCodigo());
                psDelPl.executeUpdate();
                psDelFrec.setString(1, linea.getCodigo());
                psDelFrec.executeUpdate();
            }

            //Ahora que no hay dependencias, borramos la linea de la tabla padre
            String sqlDelLinea = "DELETE FROM \"colectivo_RW\".linea WHERE codigo = ?";
            try (PreparedStatement psDelLinea = con.prepareStatement(sqlDelLinea)) {
                psDelLinea.setString( 1,linea.getCodigo());
                int filasAfectadas = psDelLinea.executeUpdate();

                if (filasAfectadas > 0) {
                    LOGGER.info("Linea " + linea.getCodigo() + " borrada correctamente de la BD: " + linea.getNombre());
                } else {
                    LOGGER.warn("No se encontró la linea para borrar en la BD: " + linea.getCodigo() + " - " + linea.getNombre());
                }
            }
            con.commit(); // Guardamos la transacción si todo salió bien
            this.actualizar = true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("Error al hacer rollback de la transacción en LineaDAOBD...", ex);
                }
            }
            throw new RuntimeException("Error al establecer la conexión en LineaDAOBD...", e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restauramos el modo de autocommit para futuras operaciones
                    //con.close();
                } catch (SQLException e) {
                    LOGGER.error("Error al cerrar la conexión en LineaDAOBD...", e);
                }
            }
        }
    }

    /**
     * Aca se llama al metodo leerDesdeBD() para cargar las lineas desde la base de datos, si el flag actualizar es true
     * o el mapa de lineas es null, se carga el mapa de lineas con los datos de la base de datos, y se setea el flag
     * actualizar a false para indicar que las lineas ya estan actualizadas. Si el flag actualizar es false y el mapa
     * de lineas no es null, se devuelve el mapa de lineas cargado previamente sin volver a cargarlo desde
     * la base de datos.
      * @return un mapa con todas las lineas cargadas, con su codigo como clave y al objeto linea como valor.
     */
    @Override
    public Map<String, Linea> buscarTodos() {
        if (actualizar || lineasMap == null) {
            this.lineasMap = leerDesdeBD();
            this.actualizar = false;
        }
        return this.lineasMap;
    }

    /**
     * Metodo privado para cargar las paradas desde la base de datos utilizando el ParadaDAO, se obtiene una instancia
     * del ParadaDAO desde la Factory, y se llama al metodo buscarTodos() para obtener un mapa con todas las paradas
     * cargadas, con su codigo como clave y al objeto parada como valor.
     * @return un mapa con todas las paradas cargadas, con su codigo como clave y al objeto parada como valor.
     */
    private Map<Integer, Parada> cargarParadas() {
        try {
            ParadaDAO paradaDAO = Factory.getInstancia("PARADA", ParadaDAO.class);
            return paradaDAO.buscarTodos();
        } catch (Exception e) {
            LOGGER.fatal("Error al obtener ParadaDAO desde la Factory en LineaDAO: ", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Metodo privado para cargar las lineas desde la base de datos, se hace la conexion a la base de datos utilizando
     * ConexionBD.getConnection(), se definen las consultas SQL para obtener las lineas, las paradas relacionadas y las
     * frecuencias, se preparan las consultas con PreparedStatement y se ejecutan para obtener los ResultSet correspondientes.
     * @return
     */
    private Map<String, Linea> leerDesdeBD() {
        Map<String, Linea> lineasMap = new LinkedHashMap<>();

        if (this.paradasCargadas == null || this.paradasCargadas.isEmpty()) {
            LOGGER.warn("No se han cargado paradas en LineaDAOBD, las lineas no se podran cargar correctamente.");
            return Collections.emptyMap();
        }
        Connection con = ConexionBD.getConnection();
        try  {
            String sqlLineas = "SELECT codigo, nombre FROM \"colectivo_RW\".linea";
            try (PreparedStatement ps = con.prepareStatement(sqlLineas);
                 ResultSet rsl = ps.executeQuery()) {

                while (rsl.next()) {
                    String codigo = rsl.getString("codigo");
                    String nombre = rsl.getString("nombre");
                    Linea linea = new Linea(codigo, nombre);
                    lineasMap.put(codigo, linea);
                }
            }

            if (lineasMap.isEmpty()) {
                return Collections.emptyMap();
            }

            String sqlPl = "SELECT codigo_linea, id_parada, orden FROM \"colectivo_RW\".linea_parada " +
                    "ORDER BY codigo_linea, orden ASC";
            try (PreparedStatement ps = con.prepareStatement(sqlPl);
                 ResultSet rspl = ps.executeQuery()) {

                while (rspl.next()) {
                    String codigoLinea = rspl.getString("codigo_linea" );
                    int idParada = rspl.getInt("id_parada");
                    Linea linea = lineasMap.get(codigoLinea);
                    Parada parada = paradasCargadas.get(idParada);

                    if (linea != null && parada != null) {
                        linea.getParadas().add(parada);

                        //Se me olvide de decir sobre las paradas, a que linea perteneces, asi que lo hago aca
                        if (!parada.getLineas().contains(linea)) {
                            parada.getLineas().add(linea);
                        }
                    }
                }
            }

            String sqlFrec = "SELECT codigo_linea, dia_semana, hora FROM \"colectivo_RW\".linea_frecuencia ORDER BY codigo_linea, dia_semana, hora ASC";
            try (PreparedStatement ps = con.prepareStatement(sqlFrec);
                 ResultSet rsFrec = ps.executeQuery()) {

                while (rsFrec.next()) {
                    String codigoLinea = rsFrec.getString("codigo_linea");
                    int diaSemana = rsFrec.getInt("dia_semana");
                    LocalTime hora = rsFrec.getTime("hora").toLocalTime();
                    Linea linea = lineasMap.get(codigoLinea);

                    if (linea != null) {
                        linea.agregarFrecuencia(diaSemana, hora);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar las lineas...", e);
            throw new RuntimeException("Error fatal en LineaDAOBD", e);
        }
        return lineasMap;
    }
}