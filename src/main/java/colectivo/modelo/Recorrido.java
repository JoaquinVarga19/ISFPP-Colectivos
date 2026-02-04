package colectivo.modelo;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Clase que representa un recorrido de una linea entre dos paradas con su hora de salida y llegada
 */
public class Recorrido {

    /*Linea que hace el recorrido*/
    private Linea linea;

    /*Parada de origen donde se inicia el recorrido*/
    private Parada origen;

    /*Parada de destino donde se finaliza el recorrido*/
    private Parada destino;

    /*Hora de salida de inicio de recorrido*/
    private LocalTime horaSalida;

    /*Hora de llegada de fin de recorrido*/
    private LocalTime horaLlegada;

    /*Duracion del recorrido*/
    private int duracion;

    /**
     * Constructor que representa a un recorrido de una linea entre dos paradas con su hora de salida y llegada
     * @param linea linea que hace el recorrido
     * @param origen origen/inicio del recorrido
     * @param destino destino/fin del recorrido
     * @param horaSalida hora de salida/inicio del recorrido
     * @param horaLlegada hora de llegada/fin del recorrido
     */
    public Recorrido(Linea linea, Parada origen, Parada destino, LocalTime horaSalida, LocalTime horaLlegada) {
        this.linea = linea;
        this.origen = origen;
        this.destino = destino;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.duracion = (int) Duration.between(horaSalida, horaLlegada).getSeconds();
    }

    /**
     * Obtiene la linea que hace el recorrido
     * @return la linea que hace el recorrido
     */
    public Linea getLinea() {
        return linea;
    }

    /**
     * Establece la linea que hace el recorrido
     * @param linea la linea que hace el recorrido
     */
    public void setLinea(Linea linea) {
        this.linea = linea;
    }

    /**
     * Obtiene el origen/inicio del recorrido
     * @return origen/inicio del recorrido
     */
    public Parada getOrigen() {
        return origen;
    }

    /**
     * Establece el origen/inicio del recorrido
     * @param origen origen del recorrido
     */
    public void setOrigen(Parada origen) {
        this.origen = origen;
    }

    /**
     * Obtiene el destino/fin del recorrido
     * @return destino/fin del recorrido
     */
    public Parada getDestino() {
        return destino;
    }

    /**
     * Establece el destino/fin del recorrido
     * @param destino destino/fin del recorrido
     */
    public void setDestino(Parada destino) {
        this.destino = destino;
    }

    /**
     * Obtiene la hora de salida de inicio del recorrido
     * @return hora de salida de inicio del recorrido
     */
    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    /**
     * Establece la hora de salida de inicio del recorrido
     * @param horaSalida hora de salida de inicio del recorrido
     */
    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * Obtiene la hora de llegada de fin del recorrido
     * @return hora de llegada de fin del recorrido
     */
    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    /**
     * Establece la hora de llegada de fin del recorrido
     * @param horaLlegada hora de llegada de fin del recorrido
     */
    public void setHoraLlegada(LocalTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    /**
     * Obtiene la duracion del recorrido en segundos
     * @return duracion del recorrido en segundos
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Establece la duracion del recorrido en segundos
     * @param duracion duracion del recorrido en segundos
     */
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    /**
     * Compara este objeto con el objeto especificado para ver si son iguales.
     * @param o el objeto de referencia con el que comparar.
     * @return true si son iguales; false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Recorrido recorrido = (Recorrido) o;
        return duracion == recorrido.duracion && Objects.equals(linea, recorrido.linea) && Objects.equals(origen, recorrido.origen) && Objects.equals(destino, recorrido.destino) && Objects.equals(horaSalida, recorrido.horaSalida) && Objects.equals(horaLlegada, recorrido.horaLlegada);
    }

    /**
     * Genera un codigo hash para el objeto.
     * @return el codigo hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(linea, origen, destino, horaSalida, horaLlegada, duracion);
    }

    /**
     * Genera una representacion en cadena del objeto.
     * @return la representacion en cadena del objeto.
     */
    @Override
    public String toString() {
        return "Recorrido{" +
                "linea=" + linea +
                ", origen=" + origen +
                ", destino=" + destino +
                ", horaSalida=" + horaSalida +
                ", horaLlegada=" + horaLlegada +
                ", duracion=" + duracion +
                '}';
    }
}
