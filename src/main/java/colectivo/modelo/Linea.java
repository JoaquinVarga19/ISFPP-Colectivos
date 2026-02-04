package colectivo.modelo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase que representa una linea de colectivo con su codigo, nombre,
 * lista de paradas por las que pasa y lista de frecuencias con las que pasa
 * por una parada
 */
public class Linea {

    /*Codigo de la linea. Ya sea L1, L2 o L3*/
    private String codigo;

    /*Nombre de la linea. Ya sea Linea1, Linea2 o Linea3*/
    private String nombre;

    /*Lista de paradas por las que pasan las distintas lineas*/
    private List<Parada> paradas;

    /*Las distintas frecuencias que pasa una linea por una parada*/
    private List<Frecuencia> frecuencias;

    /*Constructor vacio de lineas con las paradas y las frecuencias*/
    public Linea() {
        this.paradas = new ArrayList<Parada>();
        this.frecuencias = new ArrayList<Frecuencia>();
    }

    /*Constructor de una linea de colectivos con su codigo, nombre de linea, lista de paradas
    * por las que pasa y lista de las frecuencias con las que pasa en una parada*/
    public Linea(String codigo, String nombre) {
        super();
        this.codigo = codigo;
        this.nombre = nombre;
        this.paradas = new ArrayList<Parada>();
        this.frecuencias = new ArrayList<Frecuencia>();
    }

    /**
     * Agrega una parada a la linea y agrega la linea a la parada
     * @param parada La parada a agregar
     */
    public void agregarParada(Parada parada) {
        paradas.add(parada);
        parada.agregarLinea(this);
    }

    /**
     * Agrega una frecuencia a la linea
     * @param diaSemana el dia de la semana (1-7)
     * @param hora horario que esta en la lista de frecuencia
     */
    public void agregarFrecuencia(int diaSemana, LocalTime hora) {
        this.frecuencias.add(new Frecuencia(diaSemana, hora));
    }

    /**
     * Obtiene el codigo de la linea
     * @return el codigo/id de la misma
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Establece el codigo de la linea
     * @param codigo el codigo/id de la misma
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el nombre de la linea
     * @return el nombre de la linea ya sea Linea1, Linea2 o Linea3
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la linea
     * @param nombre el nombre de la linea ya sea Linea1, Linea2 o Linea3
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la lista de paradas por las que pasa la linea
     * @return las paradas por la que pasa cierta linea
     */
    public List<Parada> getParadas() {
        return paradas;
    }

    /**
     * Obtiene la lista de frecuencias de la linea
     * @return las frecuencias de la linea que pasan por una parada
     */
    public List<Frecuencia> getFrecuencias() {
        return frecuencias;
    }

    /**
     * Indica si dos lineas son iguales comparando sus codigos
     * @param o el objeto a comparar
     * @return el objeto ya comparado
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Linea linea = (Linea) o;
        return Objects.equals(codigo, linea.codigo);
    }

    /**
     * Genera un codigo hash basado en el codigo de la linea
     * @return el codigo hash de la linea
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }

    /**
     * Genera una representacion en cadena de la linea
     * @return la representacion en cadena de la linea
     */
    @Override
    public String toString() {
        return "Linea{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    /**
     * Clase interna dentro de la clase Linea que representa con que frecuencia
     * para una linea por una o mas paradas en dias y horarios determinados
     */
    public class Frecuencia {

        /*dia de la semana en los o el que pasa la linea*/
        private int diaSemana;

        /*horarios en los que pasa una linea*/
        private LocalTime hora;

        /**
         * Constructor de la clase frecuencia con el dia de la semana y el horario
         */
        public Frecuencia(int diaSemana, LocalTime hora) {
            super();
            this.diaSemana = diaSemana;
            this.hora = hora;
        }

        /**
         * Obtiene el dia de la semana
         * @return el dia de la semana
         */
        public int getDiaSemana() {
            return diaSemana;
        }

        /**
         * Obtiene el o los horarios de la linea
         * @return el horario de la linea
         */
        public LocalTime getHora() {
            return hora;
        }

        /**
         * Establece el dia de la semana
         * @param diaSemana el dia de la semana
         */
        public void setDiaSemana(int diaSemana) {
            this.diaSemana = diaSemana;
        }

        /**
         * Establece el horario de la linea
         * @param hora el horario de la linea
         */
        public void setHora(LocalTime hora) {
            this.hora = hora;
        }

        /**
         * Genera una representacion en cadena de la frecuencia
         * @return la representacion en cadena de la frecuencia
         */
        @Override
        public String toString() {
            return "Frecuencia [diaSemana=" + diaSemana + ", hora=" + hora + "]";
        }
    }
}