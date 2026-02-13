package colectivo.servicio;

import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;

import java.util.Map;

/**
 * Aplicamos el patron Facade (Fachada) para delegar a los distintos DAOs y a su vez se definen los metodos de servicio
 * para lineas, paradas y tramos
 */
public interface ColectivoService {

    //CRUD Parada
    void insertarParada(Parada parada);
    void actualizarParada(Parada parada);
    void borrarParada(Parada parada);
    Map<Integer, Parada> buscarTodosParada();

    //CRUD Linea
    void insertarLinea(Linea linea);
    void actualizarLinea(Linea linea);
    void borrarLinea(Linea linea);
    Map<String, Linea> buscarTodosLinea();

    //CRUD Tramo
    void insertarTramo(Tramo tramo);
    void actualizarTramo(Tramo tramo);
    void borrarTramo(Tramo tramo);
    Map<String, Tramo> buscarTodosTramo();
}