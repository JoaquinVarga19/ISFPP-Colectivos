package colectivo.interfaz.impl.consola;

import colectivo.aplicacion.Tiempo;
import colectivo.controlador.CoordinadorApp;
import colectivo.interfaz.Interfaz;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementación de la interfaz de usuario para consola. Esta clase se encarga de interactuar con el usuario a través
 * de la consola, solicitando la información necesaria para realizar las búsquedas de rutas, y mostrando los resultados
 * obtenidos. Utiliza un Scanner para leer la entrada del usuario, y un Logger para registrar eventos y errores
 * relacionados con la interacción con el usuario.
 */
public class InterfazConsolaImpl implements Interfaz {

    /**
     * Se define un Logger para registrar eventos relacionados con la interacción con el usuario, y para registrar
     * cualquier error que pueda ocurrir durante este proceso.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Referencia al coordinador de la aplicación, que se utiliza para orquestar la interacción entre la interfaz
     * y el modelo, y para acceder a la lógica de negocio de la aplicación. Sirve como una interaccion con el Coordinador.
     */
    private CoordinadorApp coordinadorApp;

    /**
     * Scanner para leer la entrada del usuario desde la consola. Se define como una constante estática para evitar
     * la necesidad de crear múltiples instancias del Scanner, y para garantizar que se cierre correctamente
     * al finalizar la aplicación.
     */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Inicia la interfaz de usuario, solicitando al usuario la información necesaria para realizar las búsquedas de rutas,
     * y mostrando los resultados obtenidos. Si el coordinador de la aplicación no ha sido inicializado, se muestra un
     * mensaje de error y no se inicia la interfaz.
     */
    @Override
    public void iniciar() {

        if (coordinadorApp == null) {
            System.err.println("Error: Coordinador no inyectado en la interfaz.");
            return;
        }

        System.out.println("\n*****************************************");
        System.out.println("   " + coordinadorApp.getConfiguracion().getTexto("app.titulo"));
        System.out.println("*****************************************\n");

        boolean salir = false;
        while (!salir) {
            try {
                List<Parada> lista = coordinadorApp.getListaParadas();
                Map<Integer, Parada> mapa = new HashMap<>();
                lista.forEach(p -> mapa.put(p.getCodigo(), p));

                Parada origen = ingresarParadaOrigen(mapa);
                Parada destino = ingresarParadaDestino(mapa);
                int dia = ingresarDiaSemana();
                LocalTime hora = ingresarHoraDeLlegada();

                coordinadorApp.ejecutarCalculo(origen, destino, dia, hora.toString());
                resultado(coordinadorApp.getRecorridoSolucion(), origen, destino, dia, hora);

                System.out.print("\n¿Desea realizar otra consulta? (S/N): ");
                String respuesta = sc.next();
                if (respuesta.equalsIgnoreCase("N")) {
                    salir = true;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                sc.nextLine(); // Limpiar buffer
            }
        }
        System.out.println("Aplicación finalizada.");
    }

    /**
     * Inicializamos el constructor de la aplicacion.
     * @param coordinadorApp El coordinador de la aplicación que se le pasa a esta interfaz para que pueda interactuar
     * con el resto de la arquitectura.
     */
    @Override
    public void setCoordinadorApp(CoordinadorApp coordinadorApp) {
        this.coordinadorApp = coordinadorApp;
    }

    /**
     * El usuario ingresa la parada de origen, y se valida que la parada exista en el sistema. Si la parada no existe,
     * se muestra un mensaje de error y se solicita nuevamente la entrada.
     * @param paradas
     * @return
     */
    public Parada ingresarParadaOrigen(Map<Integer, Parada> paradas) {
        System.out.println("===SELECCION DE PARADA DE ORIGEN===");

        while (true) {
            System.out.println("Ingrese el codigo de la parada de origen: ");
            int codigo = leerEnteroSeguro();

            Parada parada = paradas.get(codigo);
            if (parada != null) {
                return parada;
            } else {
                LOGGER.error("ParadaDestino: Parada no encontrada. Por favor, ingrese un código de parada válido.");
            }
        }
    }

    /**
     * El usuario ingresa la parada de destino, y se valida que la parada de destino exista en el sistema, si la parada
     * no existe, se muestra un mensaje de error y se solicita nuevamente la entrada.
     * @param paradas
     * @return
     */
    public Parada ingresarParadaDestino(Map<Integer, Parada> paradas) {
        System.out.println("===SELECCION DE PARADA DE DESTINO===");

        while (true) {
            System.out.println("Ingrese el codigo de la parada de destino: ");
            int codigo = leerEnteroSeguro();

            Parada parada = paradas.get(codigo);
            if (parada != null) {
                return parada;
            } else {
                LOGGER.error("Parada no encontrada. Por favor, ingrese un código de parada válido.");
            }
        }
    }

    /**
     * El usuario ingresa el día de la semana, y se valida que el día ingresado sea válido
     * (entre 1 y 7 o lunes, martes, miercoles,jueves, viernes, sabado, domingo/feriado).
     * Si el día no es válido, se muestra un mensaje de error y se solicita nuevamente la entrada.
     * @return
     */
    public int ingresarDiaSemana() {
        System.out.println("===INGRESO DE DIA DE LA SEMANA===");
        System.out.println("Ingrese el día de la semana (1 = Lunes ... 7 = Domingo/Feriado): ");

        int dia = leerEnteroSeguro();
        while (dia < 1 || dia > 7) {
            LOGGER.warn("Día no válido. Por favor, ingrese un número entre 1 y 7 (1 = Lunes ... 7 = Domingo/Feriado).");
            dia = leerEnteroSeguro();
        }
        return dia;
    }

    /**
     * El usuario ingresa la hora de llegada, en formato (Hh:Mh), y se valida que la hora ingresada sea válida
     * (entre 00:00 y 23:59). Si la hora no es válida, se muestra un mensaje de error y se solicita nuevamente la entrada.
     * @return
     */
    public LocalTime ingresarHoraDeLlegada() {
        System.out.println("===INGRESO DE HORA DE LLEGADA===");
        System.out.print("Ingrese la hora de llegada a la parada (formato HH:mm): ");

        while (true) {
            try {
                String imput = sc.next();
                return LocalTime.parse(imput);
            } catch (Exception e) {
                LOGGER.error("Hora no válida. Por favor, ingrese la hora en formato HH:mm (ejemplo: 14:30).");
            }
        }
    }

    /**
     * Muestra el resultado del recorrido obtenido, incluyendo las paradas intermedias, los horarios de llegada y salida,
     * y el tiempo total del recorrido.
     */
    public void resultado(List<List<Recorrido>> listaRecorridos,
                          Parada origen, Parada destino, int dia, LocalTime horaLlegadaParada) {
        System.out.println("\n==============================");
        System.out.println("Parada origen: " + origen.getDireccion());
        System.out.println("Parada destino: " + destino.getDireccion());
        System.out.println("Hora llegada pasajero: " + horaLlegadaParada);
        System.out.println("==============================");

        if (listaRecorridos == null || listaRecorridos.isEmpty()) {
            System.out.println("No se encontraron recorridos disponibles para los criterios ingresados.");
            return;
        }

        int numeroOpcion = 1;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        for (List<Recorrido> recorrido : listaRecorridos) {
            System.out.println("\n--- OPCIÓN DE RUTA #" + numeroOpcion + " ---");

            int duracionTotalSegundos = 0;
            LocalTime reloj = horaLlegadaParada; // Nuestro reloj

            for (Recorrido r : recorrido) {
                String linea = (r.getLinea() != null) ? r.getLinea().getNombre() : "Caminando";
                System.out.println("Linea: " + linea);
                System.out.println("Paradas: " + r.getOrigen().getDireccion() + " → " + r.getDestino().getDireccion());

                System.out.println("Hora de salida: " + reloj.format(formato));

                reloj = Tiempo.sumarSegundos(reloj, r.getDuracion());

                System.out.println("Hora de llegada: " + reloj.format(formato));

                System.out.println("Duración: " + Tiempo.formatearDuracionTramo(r.getDuracion()) + " min");
                System.out.println("--------------------------");

                duracionTotalSegundos += r.getDuracion();
            }

            String tiempoTotal = Tiempo.formatearTiempoTotal(duracionTotalSegundos);

            System.out.println(">> TIEMPO TOTAL DE VIAJE: " + tiempoTotal + " hs <<");
            System.out.println(">> HORA DE LLEGADA AL DESTINO FINAL: " + reloj.format(formato) + " <<\n");
            numeroOpcion++;
        }
    }

    /**
     * Muestra la lista de paradas disponibles en el sistema.
     */
    private void mostrarParadasDisponibles(Map<Integer, Parada> paradas) {
        System.out.println("Paradas disponibles:");
        for (Parada p : paradas.values()) {
            System.out.println("Código: " + p.getCodigo() + " → " + p.getDireccion());
        }
        System.out.println();
    }

    /**
     * Lee un entero de forma segura.
     * @return
     */
    private int leerEnteroSeguro() {
        while (true) {
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
                sc.next(); // Limpiar el buffer del Scanner
            }
        }
    }
}