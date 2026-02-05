package colectivo.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Clase que representa una parada de colectivo con su codigo, direccion,
 * lista de lineas que pasan por la parada, lista de paradas a las que se puede
 * llegar caminando desde esta parada, latitud y longitud
 */
public class Parada {

    /*codigo/id de la parada el cual es unico*/
    private int codigo;

    /*direccion de la parada*/
    private String direccion;

    /*lista de lineas que pasan en las distintas paradas*/
    private List<Linea> lineas;

    /*lista de paradas por las que se puede ir caminando, es decir,
    * las paradas que estan cerca tuyo*/
    private List<Parada> paradasCaminando;

    /*latitud de la parada*/
    private double latitud;

    /*longitud de la parada*/
    private double longitud;

    /**
     * Constructor vacio de parada con las lineas y paradas caminando
     */
    public Parada() {
        this.lineas = new ArrayList<Linea>();
        this.paradasCaminando = new ArrayList<Parada>();
    }

    /**
     * Constructor de una parada de colectivo con su codigo, direccion,
     * latitud, longitud y las listas de lineas y paradas caminando declaradas en un arraylist
     * @param codigo codigo/id de la parada
     * @param direccion direccion de la parada
     * @param latitud latitud de la parada
     * @param longitud longitud de la parada
     */
    public Parada(int codigo, String direccion, double latitud, double longitud) {
        super();
        this.codigo = codigo;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.lineas = new ArrayList<Linea>();
        this.paradasCaminando = new ArrayList<Parada>();
    }

    /**
     * Agrega una linea a la parada
     * @param linea la linea a agregar a la parada
     */
    public void agregarLinea(Linea linea) {
        this.lineas.add(linea);
    }

    /**
     * Agrega una parada a la lista de paradas a las que se puede llegar caminando
     * @param parada la parada a agregar
     */
    public void agregarParadaCaminando(Parada parada) {
        this.paradasCaminando.add(parada);
    }

    /**
     * Obtiene el codigo de la parada
     * @return el codigo/id de la parada
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Establece el codigo de la parada
     * @param codigo el codigo/id de la parada
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene la direccion de la parada
     * @return la direccion de la parada
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la direccion de la parada
     * @param direccion la direccion de la parada
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene la latitud de la parada
     * @return la latitud de la parada
     */
    public double getLatitud() {
        return latitud;
    }

    /**
     * Establece la latitud de la parada
     * @param latitud la latitud de la parada
     */
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    /**
     * Obtiene la longitud de la parada
     * @return la longitud de la parada
     */
    public double getLongitud() {
        return longitud;
    }

    /**
     * Establece la longitud de la parada
     * @param longitud la longitud de la parada
     */
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    /**
     * Obtiene la lista de lineas que pasan por la parada
     * @return las lineas que pasan por la parada
     */
    public List<Linea> getLineas() {
        return lineas;
    }

    /**
     * Obtiene la lista de paradas a las que se puede llegar caminando desde esta parada
     * @return las paradas a las que se puede llegar caminando
     */
    public List<Parada> getParadasCaminando() {
        return paradasCaminando;
    }

    /**
     * Representacion en String de la parada
     * @return la parada en formato String
     */
    @Override
    public String toString() {
        return "Parada{" +
                "codigo='" + codigo + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }

    /**
     * Indica si dos paradas son iguales comparando su codigo y direccion
     * @param o el objeto de referencia con el que comparar.
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Parada parada = (Parada) o;
        return Objects.equals(codigo, parada.codigo) && Objects.equals(direccion, parada.direccion);
    }

    /**
     * Genera un codigo hash basado en el codigo y direccion de la parada
     * @return el codigo hash de la parada
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigo, direccion);
    }
}
