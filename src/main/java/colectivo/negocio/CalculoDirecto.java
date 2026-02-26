package colectivo.negocio;

import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Recorrido;
import colectivo.modelo.Tramo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Esta clase implementa el algoritmo de recorrido directo, que calcula el camino directo entre el origen y el destino,
 * sin tener en cuenta las paradas intermedias, buscando una sola linea.
 */
public class CalculoDirecto implements AlgoritmoRecorrido {

    /**
     * Calcula el recorrido directo entre una parada origen y una parada destino.
     * El algoritmo recorre las lineas que pasan por la parada de origen y verifica si alguna de esas lineas también
     * pasa por la parada de destino.
     * Si encuentra una linea que pasa por ambas paradas, construye un recorrido directo entre ellas, utilizando los
     * tramos disponibles para calcular la duracion de cada tramo del recorrido.
     * @param paradaOrigen
     * @param paradaDestino
     * @param diaSemana
     * @param horaLlegada
     * @param tramos
     * @return
     */
    @Override
    public List<List<Recorrido>> calcularRecorrido(Parada paradaOrigen, Parada paradaDestino, int diaSemana,
                                                   LocalTime horaLlegada, Map<String, Tramo> tramos) {
        List<List<Recorrido>> soluciones = new ArrayList<>();

        for (Linea linea : paradaOrigen.getLineas()) {

            //Lista de paradas por las que pasa una linea.
            List<Parada> paradasDeEstaLinea = linea.getParadas();

            //Indices de parada y origen
            int indiceOrigen = paradasDeEstaLinea.indexOf(paradaOrigen);
            int indiceDestino = paradasDeEstaLinea.indexOf(paradaDestino);

            if (indiceOrigen != -1 && indiceDestino != -1 && indiceOrigen < indiceDestino) {
                List<Recorrido> opcionDirecta = new ArrayList<>();
                for (int i = indiceOrigen; i < indiceDestino; i++) {
                    Parada actual = paradasDeEstaLinea.get(i);
                    Parada siguiente = paradasDeEstaLinea.get(i + 1);

                    //Construimos el ID del tramo utilizando el formato "origen-destino-tipo", donde tipo es 1 para colectivo.
                    String tramoId = actual.getCodigo() + "-" + siguiente.getCodigo() + "-1";
                    Tramo tramoInfo = tramos.get(tramoId);

                    //Confirmacion de seguridad
                    if (tramoInfo != null) {
                        Recorrido tramoRecorrido = new Recorrido();
                        tramoRecorrido.setOrigen(actual);
                        tramoRecorrido.setDestino(siguiente);
                        tramoRecorrido.setLinea(linea);

                        tramoRecorrido.setDuracion(tramoInfo.getTiempo());
                        //agregamos el tramo al recorrido directo
                        opcionDirecta.add(tramoRecorrido);
                    } else {
                        // Si no se encuentra el tramo, se omite esta opción directa
                        opcionDirecta.clear();
                        break;
                    }
                }
                if (!opcionDirecta.isEmpty()) {
                    soluciones.add(opcionDirecta);
                }
            }
        }
        return soluciones;
    }
}