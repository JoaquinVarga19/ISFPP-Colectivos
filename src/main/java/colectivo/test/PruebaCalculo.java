package colectivo.test;

import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.dao.ParadaDAO;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;
import colectivo.negocio.Calculo;
import colectivo.negocio.CalculoCaminando;
import colectivo.negocio.CalculoDijkstra;
import colectivo.negocio.CalculoDirecto;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class PruebaCalculo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Usamos Locale.US para que acepte puntos en coordenadas si fuera necesario
        sc.useLocale(Locale.US);

        try {
            System.out.println("=== SISTEMA DE TRANSPORTE - MODO PRUEBA ===");

            // 1. CARGA DE DATOS DESDE ARCHIVOS
            ParadaDAO paradaDAO = Factory.getInstancia("PARADA", ParadaDAO.class);
            TramoDAO tramoDAO = Factory.getInstancia("TRAMO", TramoDAO.class);
            LineaDAO lineaDAO = Factory.getInstancia("LINEA", colectivo.dao.LineaDAO.class);

            Map<Integer, Parada> todasLasParadas = paradaDAO.buscarTodos();
            Map<String, Tramo> todosLosTramos = tramoDAO.buscarTodos();
            lineaDAO.buscarTodos();

            // 2. INGRESO DE ORIGEN Y DESTINO
            Parada origen = solicitarParada(sc, todasLasParadas, "ORIGEN");
            Parada destino = solicitarParada(sc, todasLasParadas, "DESTINO");

            if (origen == null || destino == null) return;

            // 3. INGRESO DE DÍA Y HORA
            System.out.println("\nDías: 1:Lun, 2:Mar, 3:Mie, 4:Jue, 5:Vie, 6:Sab, 7:Dom/Fer");
            System.out.print("Ingrese día (número): ");
            int dia = sc.nextInt();

            System.out.print("Ingrese horario (formato HH:mm, ej 08:30): ");
            String horaStr = sc.next();
            LocalTime horaActual = LocalTime.parse(horaStr);

            // 4. EJECUCIÓN DEL MOTOR DE CÁLCULO (Strategy)
            Calculo calculador = new Calculo(new CalculoDijkstra());

            System.out.println("\n" + "=".repeat(50));
            System.out.println("RECORRIDOS: " + origen.getDireccion() + " -> " + destino.getDireccion());
            System.out.println("=".repeat(50));

            // --- OPCIÓN A: DIJKSTRA (Combinaciones) ---
            System.out.println("\n>>> OPCIÓN 1: CAMINO MÁS CORTO (Dijkstra)");
            imprimirCamino(calculador.ejecutarCalculo(origen, destino, dia, horaActual, todosLosTramos));

            // --- OPCIÓN B: DIRECTO (Sin trasbordos) ---
            System.out.println("\n>>> OPCIÓN 2: COLECTIVO DIRECTO (Sin trasbordos)");
            calculador.setEstrategia(new CalculoDirecto());
            imprimirCamino(calculador.ejecutarCalculo(origen, destino, dia, horaActual, todosLosTramos));

            // --- OPCIÓN C: CAMINANDO ---
            System.out.println("\n>>> OPCIÓN 3: CAMINANDO");
            calculador.setEstrategia(new CalculoCaminando());
            imprimirCamino(calculador.ejecutarCalculo(origen, destino, dia, horaActual, todosLosTramos));

        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            System.out.println("Verifique que el formato de hora sea HH:mm y los IDs existan.");
        }
    }

    private static Parada solicitarParada(Scanner sc, Map<Integer, Parada> paradas, String tipo) {
        System.out.print("\nIngrese " + tipo + " (ID o parte de la Dirección): ");
        String entrada = sc.next();

        // Búsqueda por ID
        if (entrada.matches("\\d+")) {
            Parada p = paradas.get(Integer.parseInt(entrada));
            if (p != null) return p;
        }

        // Búsqueda por texto en Dirección
        for (Parada p : paradas.values()) {
            if (p.getDireccion().toLowerCase().contains(entrada.toLowerCase())) {
                System.out.println("Seleccionada: " + p.getDireccion() + " (ID: " + p.getCodigo() + ")");
                return p;
            }
        }

        System.out.println("No se encontró la parada: " + entrada);
        return null;
    }

    private static void imprimirCamino(List<List<Recorrido>> soluciones) {
        if (soluciones == null || soluciones.isEmpty()) {
            System.out.println("  No hay recorridos disponibles para esta opción.");
            return;
        }

        // Mostramos la primera solución (la más óptima de esa estrategia)
        List<Recorrido> camino = soluciones.get(0);
        int tiempoTotalSegundos = 0;

        for (Recorrido r : camino) {
            String lineaLabel = (r.getLinea() != null) ? r.getLinea().getNombre() : "A PIE";
            System.out.println("  [" + lineaLabel + "]");
            System.out.println("  " + r.getOrigen().getDireccion() + " -> " + r.getDestino().getDireccion());

            int seg = r.getDuracion();
            tiempoTotalSegundos += seg;
            System.out.println("  Duración: " + (seg / 60) + "m " + (seg % 60) + "s");
            System.out.println("  " + "-".repeat(30));
        }

        System.out.println("  >> TIEMPO TOTAL ESTIMADO: " + (tiempoTotalSegundos / 60) + "m " + (tiempoTotalSegundos % 60) + "s");
    }
}