package colectivo.conexion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Clase que se encarga de manejar la conexion a la base de datos, utilizando un singleton para mantener una unica conexion durante toda la ejecucion del programa
 * y un shutdown hook para cerrar la conexion al finalizar el programa de manera segura
 */
public class ConexionBD {
    /**
     * conexion a la base de datos, se mantiene una unica conexion durante toda la ejecucion del programa
     */
    private static Connection con = null;

    /**
     * Logger para registrar eventos y errores relacionados con la conexión a la base de datos
     */
    private static final Logger LOGGER = LogManager.getLogger(ConexionBD.class);

    // Nos conectamos a la base de datos (con los datos de conexión del archivo
    // jdbc.properties)
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                // con esto determinamos cuando finalize el programa
                Runtime.getRuntime().addShutdownHook(new MiShDwnHook());
                ResourceBundle rb = ResourceBundle.getBundle("jdbc");
                String driver = rb.getString("driver");
                String url = rb.getString("url");
                String usr = rb.getString("usr");
                String pwd = rb.getString("pwd");
                String schema = rb.getString("schema");
                Class.forName(driver);
                con = DriverManager.getConnection(url, usr, pwd);
                Statement statement = con.createStatement();
                try {
                    statement.execute("set search_path to '" + schema + "'");
                } finally {
                    statement.close();
                }
            }
            return con;
        } catch (Exception ex) {
            LOGGER.fatal("No se pudo crear la conexión a la BD.", ex);
            throw new RuntimeException("Error al crear la conexion", ex);
        }
    }

    /**
     * Clase interna que se encarga de cerrar la conexion a la base de datos al finalizar el programa, utilizando
     * un shutdown hook para asegurar que se cierre de manera segura
     */
    public static class MiShDwnHook extends Thread {
        // justo antes de finalizar el programa la JVM invocara
        // a este metodo donde podemos cerrar la conexion
        public void run() {
            try {
                Connection con = ConexionBD.getConnection();
                con.close();
                LOGGER.info("Conexion a la BD cerrada correctamente.");
            } catch (Exception ex) {
                LOGGER.error("Error intentando cerrar la conexión a la BD.", ex);
                throw new RuntimeException(ex);
            }
        }
    }
}