package colectivo.test;

import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.dao.ParadaDAO;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;
import colectivo.negocio.Calculo;
import colectivo.negocio.CalculoCaminando;
import colectivo.negocio.CalculoDijkstra;
import colectivo.negocio.CalculoDirecto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestCalcularRecorridoDAO {

    private Map<Integer, Parada> paradas;
    private Map<String, Linea> lineas;
    private Map<String, Tramo> tramos;

    private int diaSemana;
    private LocalTime horaLlegaParada;

    private Calculo calculo;

    @BeforeEach
    void setUp() throws Exception {
        // Inicialización usando tus DAOs y Factory
        paradas = Factory.getInstancia("PARADA", ParadaDAO.class).buscarTodos();
        lineas = Factory.getInstancia("LINEA", LineaDAO.class).buscarTodos();
        tramos = Factory.getInstancia("TRAMO", TramoDAO.class).buscarTodos();

        diaSemana = 1; // Lunes
        horaLlegaParada = LocalTime.of(10, 0);

        // Configuramos el Contexto con tu Dijkstra
        calculo = new Calculo(new CalculoDijkstra());
    }

    @Test
    void testCalcularRecorridoDijkstra() {
        // IMPORTANTE: Asegurate que estos IDs existan en tus archivos .txt
        Parada origen = paradas.get(31);
        Parada destino = paradas.get(75);

        assertNotNull(origen, "La parada 31 no se encontró en los datos");
        assertNotNull(destino, "La parada 75 no se encontró en los datos");

        // Ejecución a través del Contexto
        List<List<Recorrido>> soluciones = calculo.ejecutarCalculo(origen, destino, diaSemana, horaLlegaParada, tramos);

        assertNotNull(soluciones);
        assertTrue(soluciones.size() > 0, "Dijkstra no encontró ningún camino");

        List<Recorrido> mejorCamino = soluciones.get(0);

        // Verificamos que el inicio del primer tramo coincida con el origen
        assertEquals(origen.getCodigo(), mejorCamino.get(0).getOrigen().getCodigo());

        // Verificamos que el fin del último tramo coincida con el destino
        Recorrido ultimoTramo = mejorCamino.get(mejorCamino.size() - 1);
        assertEquals(destino.getCodigo(), ultimoTramo.getDestino().getCodigo());

        // Verificamos que todos los tramos tengan una línea o sean tramos válidos
        for (Recorrido paso : mejorCamino) {
            assertNotNull(paso.getOrigen());
            assertNotNull(paso.getDestino());
        }
    }

    @Test
    void testCalcularRecorridoCaminando() {
        // Configuramos la estrategia de caminar
        calculo.setEstrategia(new CalculoCaminando());

        Parada origen = paradas.get(70);
        Parada destino = paradas.get(75);

        List<List<Recorrido>> soluciones = calculo.ejecutarCalculo(origen, destino, diaSemana, horaLlegaParada, tramos);

        assertNotNull(soluciones);
        assertEquals(1, soluciones.size(), "Caminando debería devolver siempre una única opción");

        List<Recorrido> camino = soluciones.get(0);
        assertEquals(1, camino.size(), "El camino a pie debe ser un único tramo directo");

        Recorrido tramoUnico = camino.get(0);
        assertEquals(origen.getCodigo(), tramoUnico.getOrigen().getCodigo());
        assertEquals(destino.getCodigo(), tramoUnico.getDestino().getCodigo());

        // Verificamos que sea la línea de peatón (usando tus constantes)
        assertNotNull(tramoUnico.getLinea());
        assertEquals("A PIE", tramoUnico.getLinea().getNombre());
    }

    @Test
    void testCalcularRecorridoDirecto() {
        // Configuramos la estrategia de colectivo directo (sin transbordos)
        calculo.setEstrategia(new CalculoDirecto());

        // Elegimos dos paradas que sepamos que pertenecen a la misma línea
        // Por ejemplo, de la Línea 2 de Madryn (L2R)
        Parada origen = paradas.get(80);
        Parada destino = paradas.get(7); // Una parada cercana en la misma línea

        List<List<Recorrido>> soluciones = calculo.ejecutarCalculo(origen, destino, diaSemana, horaLlegaParada, tramos);

        assertNotNull(soluciones);
        assertTrue(soluciones.size() > 0, "Debería encontrar al menos una línea directa");

        // Verificamos que el primer camino encontrado sea coherente
        List<Recorrido> caminoDirecto = soluciones.get(0);

        // El inicio del primer tramo debe ser nuestro origen
        assertEquals(origen.getCodigo(), caminoDirecto.get(0).getOrigen().getCodigo());

        // El fin del último tramo debe ser nuestro destino
        assertEquals(destino.getCodigo(), caminoDirecto.get(caminoDirecto.size() - 1).getDestino().getCodigo());

        // Verificamos que todos los tramos del camino pertenezcan a la misma línea
        Linea lineaUsada = caminoDirecto.get(0).getLinea();
        for (Recorrido paso : caminoDirecto) {
            assertEquals(lineaUsada.getCodigo(), paso.getLinea().getCodigo(),
                    "En un cálculo directo, todos los tramos deben ser de la misma línea");
        }
    }
}