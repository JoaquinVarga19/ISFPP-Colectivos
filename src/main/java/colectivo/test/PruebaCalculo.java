package colectivo.test;

import colectivo.conexion.Factory;
import colectivo.dao.*;
import colectivo.modelo.*;
import colectivo.negocio.*;
import java.time.LocalTime;
import java.util.*;

public class PruebaCalculo{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("Cargando sistema de transporte...");
            // Uso de Factory con los tipos correctos
            ParadaDAO paradaDAO = Factory.getInstancia("PARADA", ParadaDAO.class);
            TramoDAO tramoDAO = Factory.getInstancia("TRAMO", TramoDAO.class);

            Map<Integer, Parada> todasLasParadas = paradaDAO.buscarTodos();
            Map<String, Tramo> todosLosTramos = tramoDAO.buscarTodos();

            // 1. INGRESO DE ORIGEN Y DESTINO
            Parada origen = buscarParada(sc, todasLasParadas, "ORIGEN");
            Parada destino = buscarParada(sc, todasLasParadas, "DESTINO");

            if (origen == null || destino == null) return;

            // 2. INGRESO DE DÍA Y HORA
            System.out.println("\nDías: 1:Lun, 2:Mar, 3:Mie, 4:Jue, 5:Vie, 6:Sab, 7:Dom/Fer");
            System.out.print("Ingrese día (número): ");
            int dia = sc.nextInt();

            System.out.print("Ingrese hora (0-23): ");
            int hora = sc.nextInt();
            System.out.print("Ingrese minutos (0-59): ");
            int min = sc.nextInt();
            LocalTime horaActual = LocalTime.of(hora, min);

            // 3. CÁLCULO Y MUESTRA DE RESULTADOS
            Calculo calculo = new Calculo(new CalculoDijkstra());

            System.out.println("\n--- RECORRIDOS DISPONIBLES ---");

            // Opción Colectivos (Dijkstra)
            imprimirCamino("OPCIÓN COLECTIVO (Dijkstra)",
                    calculo.ejecutarCalculo(origen, destino, dia, horaActual, todosLosTramos));

            // Opción Directo
            calculo.setEstrategia(new CalculoDirecto());
            imprimirCamino("OPCIÓN LÍNEA DIRECTA",
                    calculo.ejecutarCalculo(origen, destino, dia, horaActual, todosLosTramos));

            // Opción Caminando
            calculo.setEstrategia(new CalculoCaminando());
            imprimirCamino("OPCIÓN CAMINANDO",
                    calculo.ejecutarCalculo(origen, destino, dia, horaActual, todosLosTramos));

        } catch (Exception e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
        }
    }

    private static Parada buscarParada(Scanner sc, Map<Integer, Parada> paradas, String tipo) {
        System.out.print("\nIngrese " + tipo + " (Código o Dirección): ");
        String entrada = sc.next();

        // Búsqueda por código si la entrada es numérica
        if (entrada.matches("\\d+")) {
            return paradas.get(Integer.parseInt(entrada));
        }

        // Búsqueda por dirección (usando getDireccion())
        for (Parada p : paradas.values()) {
            if (p.getDireccion().equalsIgnoreCase(entrada)) {
                return p;
            }
        }
        System.out.println("No se encontró la parada: " + entrada);
        return null;
    }

    private static void imprimirCamino(String titulo, List<List<Recorrido>> soluciones) {
        System.out.println("\n" + titulo);
        System.out.println("=========================================");

        if (soluciones == null || soluciones.isEmpty()) {
            System.out.println("No hay recorridos disponibles.");
        } else {
            // Se muestra la primera solución encontrada
            List<Recorrido> camino = soluciones.get(0);
            for (Recorrido r : camino) {
                // Se usa getNombre() de la clase Linea
                String nombreLinea = (r.getLinea() != null) ? r.getLinea().getNombre() : "A PIE";

                System.out.println("Línea: " + nombreLinea);
                // Se usa getDireccion() de la clase Parada
                System.out.println(r.getOrigen().getDireccion() + " -> " + r.getDestino().getDireccion());

                // Formateo de duración
                int segundosTotales = r.getDuracion();
                int min = segundosTotales / 60;
                int seg = segundosTotales % 60;

                System.out.println("Duración: " + min + "m " + seg + "s");
                System.out.println("-----------------------------------------");
            }
        }
    }
}