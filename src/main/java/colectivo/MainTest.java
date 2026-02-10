package colectivo;

import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.dao.ParadaDAO;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Clase de prueba para verificar la carga de DAOs con modelos reales.
 * Este Main se utiliza para probar la correcta carga de los datos desde los archivos .txt utilizando los DAOs
 * implementados.
 * Se prueban las funcionalidades básicas de cada DAO, como la obtención de paradas, líneas y tramos, y se muestra
 * información relevante en la consola para verificar que los datos se han cargado correctamente.
 */
public class MainTest {
    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);

    public static void main(String[] args) {
        LOGGER.info("Iniciando prueba de carga de DAOs con modelos reales...");

        try {
            // 1. Probar Paradas (Usa getCodigo y getDireccion)
            ParadaDAO paradaDAO = Factory.getInstancia("PARADA", ParadaDAO.class);
            Map<Integer, Parada> paradasMap = paradaDAO.buscarTodos();
            System.out.println("=== TEST PARADAS ===");
            if (paradasMap.isEmpty()) {
                System.out.println("No se cargaron paradas. Revisá el archivo .txt y config.properties.");
            } else {
                //de 5 a 149
                paradasMap.values().stream().limit(149).forEach(p ->
                        System.out.println("ID: " + p.getCodigo() + " | Direccion: " + p.getDireccion()));
            }

            // 2. Probar Líneas (Usa getNombre y getParadas)
            LineaDAO lineaDAO = Factory.getInstancia("LINEA", LineaDAO.class);
            Map<String, Linea> lineasMap = lineaDAO.buscarTodos();
            System.out.println("\n=== TEST LÍNEAS ===");
            lineasMap.values().forEach(l -> {
                System.out.println("Línea: " + l.getNombre() + " | Cantidad de paradas: " + l.getParadas().size());
            });

            // 3. Probar Tramos (Usa getInicio, getFin y getTiempo)
            TramoDAO tramoDAO = Factory.getInstancia("TRAMO", TramoDAO.class);
            Map<String, Tramo> tramosMap = tramoDAO.buscarTodos();
            System.out.println("\n=== TEST TRAMOS ===");
            //de 5 a 149
            tramosMap.values().stream().limit(149).forEach(t -> {
                System.out.println("Desde: " + t.getInicio().getDireccion() +
                        " Hasta: " + t.getFin().getDireccion() +
                        " | Tiempo: " + t.getTiempo() + " min");
            });

        } catch (Exception e) {
            LOGGER.error("Error crítico en el Main de prueba: ", e);
        }
    }
}