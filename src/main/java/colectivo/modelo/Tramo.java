package colectivo.modelo;

import colectivo.aplicacion.Constantes;

import java.util.Objects;

/**
 * Clase que representa un tramo entre dos paradas, con su tiempo y tipo de tramo.
 * Entre dos paradas seria el origen desde donde inicias hasta donde queres ir que es el destino,
 * el tiempo es cuanto tardas en ir desde origen a destino y el tipo es si vas en colectivo=1 o caminando=2
 */
public class Tramo {

    /*Parada de inicio donde inicia el tramo*/
    private Parada inicio;

    /*Parada de fin donde finaliza el tramo*/
    private Parada fin;

    /*Tiempo que tarda de ir desde origen a destino*/
    private int tiempo;

    /*tipo colectivo es cuanto tarda en ir desde inicio a fin en colectivo
    * y tipo caminando cuanto se tarda caminando*/
    private int tipo;

    /**
     * constructor vacio de tramo
     */
    public Tramo() {
    }

    /**
     * Constructor de un tramo entre dos paradas con su tiempo y tipo, se verifica
     * que si el tipo es caminando se agregue la parada de fin a la lista de paradas caminando
     * de la parada de inicio y viceversa
     * @param inicio inicio del tramo
     * @param fin fin del tramo
     * @param tiempo tiempo que tarda en ir desde inicio a fin
     * @param tipo colectivo=1 y caminando=2
     */
    public Tramo(Parada inicio, Parada fin, int tiempo, int tipo) {
        super();
        this.inicio = inicio;
        this.fin = fin;
        this.tiempo = tiempo;
        this.tipo = tipo;
        if (tipo == Constantes.CAMINANDO) {
            inicio.agregarParadaCaminando(fin);
            fin.agregarParadaCaminando(inicio);
        }
    }

    /**
     * Obtiene la parada de inicio del tramo
     * @return la parada de inicio
     */
    public Parada getInicio() {
        return inicio;
    }

    /**
     * Establece la parada de inicio del tramo
     * @param inicio la parada de inicio
     */
    public void setInicio(Parada inicio) {
        this.inicio = inicio;
    }

    /**
     * Obtiene la parada de fin del tramo
     * @return la parada de fin
     */
    public Parada getFin() {
        return fin;
    }

    /**
     * Establece la parada de fin del tramo
     * @param fin la parada de fin
     */
    public void setFin(Parada fin) {
        this.fin = fin;
    }

    /**
     * Obtiene el tiempo que tarda en ir desde inicio a fin
     * @return el tiempo en segundos
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * Establece el tiempo que tarda en ir desde inicio a fin
     * @param tiempo el tiempo en segundos
     */
    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * Obtiene el tipo de tramo (colectivo o caminando)
     * @return tipo de tramo colectivo/caminando
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de tramo (colectivo o caminando)
     * @param tipo tipo de tramo colectivo/caminando
     */
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    /**
     * Compara este tramo con otro objeto para ver si son iguales (mismas paradas de inicio y fin)
     * @param o el objeto de referencia con el que comparar.
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tramo tramo = (Tramo) o;
        return Objects.equals(inicio, tramo.inicio) && Objects.equals(fin, tramo.fin);
    }

    /**
     * Genera un codigo hash basado en las paradas de inicio y fin
     * @return el codigo hash del tramo
     */
    @Override
    public int hashCode() {
        return Objects.hash(inicio, fin);
    }

    /**
     * Genera una representacion en cadena del tramo
     * @return la representacion en cadena del tramo
     */
    @Override
    public String toString() {
        return "Tramo{" +
                "inicio=" + inicio +
                ", fin=" + fin +
                ", tiempo=" + tiempo +
                ", tipo=" + tipo +
                '}';
    }
}
