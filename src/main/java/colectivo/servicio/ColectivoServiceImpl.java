package colectivo.servicio;

import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;

import java.util.Map;

/**
 * Aqui es donde se van a implementar los metodos de servicio, es la que orquesta a los tres DAOs.
 */
public class ColectivoServiceImpl implements ColectivoService {

    //----------------Parada-----------------------//

    /**
     *
     * @param parada
     */
    @Override
    public void insertarParada(Parada parada) {

    }

    /**
     *
     * @param parada
     */
    @Override
    public void actualizarParada(Parada parada) {

    }

    /**
     *
     * @param parada
     */
    @Override
    public void borrarParada(Parada parada) {

    }

    /**
     *
     * @return
     */
    @Override
    public Map<Integer, Parada> buscarTodosParada() {
        return Map.of();
    }

    //----------------Parada-----------------------//

    /**
     *
     * @param linea
     */
    @Override
    public void insertarLinea(Linea linea) {

    }

    /**
     *
     * @param linea
     */
    @Override
    public void actualizarLinea(Linea linea) {

    }

    /**
     *
     * @param linea
     */
    @Override
    public void borrarLinea(Linea linea) {

    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, Linea> buscarTodosLinea() {
        return Map.of();
    }

    //----------------Tramo-----------------------//

    /**
     *
     * @param tramo
     */
    @Override
    public void insertarTramo(Tramo tramo) {

    }

    /**
     *
     * @param tramo
     */
    @Override
    public void actualizarTramo(Tramo tramo) {

    }

    /**
     *
     * @param tramo
     */
    @Override
    public void borrarTramo(Tramo tramo) {

    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, Tramo> buscarTodosTramo() {
        return Map.of();
    }
}
