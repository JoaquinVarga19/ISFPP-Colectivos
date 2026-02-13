package colectivo.servicio;

import colectivo.conexion.Factory;
import colectivo.dao.LineaDAO;
import colectivo.dao.ParadaDAO;
import colectivo.dao.TramoDAO;
import colectivo.modelo.Linea;
import colectivo.modelo.Parada;
import colectivo.modelo.Tramo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Aqui es donde se van a implementar los metodos de servicio, es la que orquesta a los tres DAOs.
 */
public class ColectivoServiceImpl implements ColectivoService {

    /**
     * Logger para registrar eventos y errores en la aplicación, utilizando Log4j2 para facilitar el seguimiento y depuración del código.
     */
    private static final Logger LOGGER = LogManager.getLogger(ColectivoServiceImpl.class);

    /**
     * Referencia al DAO de parada para referenciar a los metodos de servicio de parada, es decir, para insertar,
     * actualizar, borrar y buscar paradas.
     * Y a su vez se lo llama desde el Factory.
     */
    private ParadaDAO paradaDAO;

    /**
     * Referencia al DAO de linea para referenciar a los metodos de servicio de linea, es decir, para insertar,
     * actualizar, borrar y buscar lineas.
      * Y a su vez se lo llama desde el Factory.
     */
    private LineaDAO lineaDAO;

    /**
     * Referencia al DAO de tramo para referenciar a los metodos de servicio de tramo, es decir, para insertar,
     * actualizar, borrar y buscar tramos.
      * Y a su vez se lo llama desde el Factory.
     */
    private TramoDAO tramoDAO;

    /**
     * Constructor de la clase ColectivoServiceImpl, donde se inicializan las referencias a los DAOs de parada, linea y tramo.
      * Y a su vez se lo llama desde el Factory.
      * Se utiliza para crear una instancia de ColectivoServiceImpl y establecer las dependencias necesarias para su funcionamiento.
      * En este caso, se podrían inyectar los DAOs a través del constructor o mediante un framework de inyección de dependencias.
      * En este ejemplo, se deja vacío para que sea el Factory quien se encargue de establecer las dependencias.
     */
    public ColectivoServiceImpl() {
        this.paradaDAO = Factory.getInstancia("PARADA", ParadaDAO.class );
        this.lineaDAO = Factory.getInstancia("LINEA", LineaDAO.class);
        this.tramoDAO = Factory.getInstancia("TRAMO", TramoDAO.class);
    }

    //----------------Parada-----------------------//

    /**
     * Llamamos al metodo de insertar parada del DAO de parada, para insertar una nueva parada
     * y verificamos que el codigo de parada exista y sea mayor que cero y que la parada exista
     * @param parada parada a insertar via servicio
     */
    @Override
    public void insertarParada(Parada parada) {
        if (parada != null && parada.getCodigo() > 0) {
            paradaDAO.insertar(parada);
            LOGGER.info("Parada insertada via servicio: " + parada);
        }
    }

    /**
     * Llamamos al metodo de actualizar parada del DAO de parada, para actualizar una parada existente
     * y verificamos que el codigo de parada exista y sea mayor que cero y que la parada exista
     * @param parada parada a actualizar via servicio
     */
    @Override
    public void actualizarParada(Parada parada) {
        if (parada != null && parada.getCodigo() > 0) {
            //antes de llamar al DAO verificamos que la parada exista
            //llamando a containsKey evito que el sistema intente actualizar una parada que no existe o no esta cargada
            if (paradaDAO.buscarTodos().containsKey(parada.getCodigo())) {
                paradaDAO.actualizar(parada);
                LOGGER.info("Parada actualizada via servicio con ID: " + parada.getCodigo());
            } else {
                LOGGER.warn("No se puede actualizar la parada, no existe con ID: " + parada.getCodigo());
            }
        }
    }

    /**
     * Llamamos al metodo de borrar parada del DAO de parada, para borrar una parada existente
     * y verificamos que el codigo de parada exista y sea mayor que cero y que la parada exista
     * En el futuro podremos aplicar: "Si esta parada está siendo usada en algún Tramo o en el recorrido de alguna Línea,
     * no permitas el borrado."
     * @param parada parada a borrar via servicio
     */
    @Override
    public void borrarParada(Parada parada) {
        if (parada != null && parada.getCodigo() > 0) {
            //antes de llamar al DAO verificamos que la parada exista
            //llamando a containsKey evito que el sistema intente borrar una parada que no existe o no esta cargada
            if (paradaDAO.buscarTodos().containsKey(parada.getCodigo())) {
                paradaDAO.borrar(parada);
                LOGGER.info("Parada borrada via servicio con ID: " + parada.getCodigo());
            } else {
                LOGGER.warn("No se puede borrar la parada, no existe con ID: " + parada.getCodigo());
            }
        } else {
            LOGGER.warn("Intento de borrar una parada nula o con código inválido.");
        }
    }

    /**
     * Llamamos al metodo de buscar todos las paradas del DAO de parada, para obtener un mapa con todas las paradas
     * existentes
     * @return mapa con todas las paradas existentes via servicio
     */
    @Override
    public Map<Integer, Parada> buscarTodosParada() {
        return paradaDAO.buscarTodos();
    }

    //----------------Parada-----------------------//

    /**
     * Llamamos al metodo de insertar linea del DAO de linea, para insertar una nueva linea
     * @param linea linea a insertar via servicio
     */
    @Override
    public void insertarLinea(Linea linea) {
        //verificamos que exista la linea, su codigo y que el mismo no este vacio
        if (linea != null && linea.getCodigo() != null && !linea.getCodigo().isEmpty()) {
            if (!lineaDAO.buscarTodos().containsKey(linea.getCodigo())) {
                lineaDAO.insertar(linea);
                LOGGER.info("Linea insertada via servicio: " + linea.getNombre());
            } else {
                LOGGER.warn("No se puede insertar la linea, ya existe con código: " + linea.getCodigo());
            }
        }
    }

    /**
     * Llamamos al metodo de actualizar linea del DAO de linea, para actualizar una linea existente
      * y verificamos que exista la linea y su codigo
     * @param linea linea a actualizar via servicio
     */
    @Override
    public void actualizarLinea(Linea linea) {
        if (linea != null && linea.getCodigo() != null) {
            if (lineaDAO.buscarTodos().containsKey(linea.getCodigo())) {
                lineaDAO.actualizar(linea);
                LOGGER.info("Linea actualizada via servicio con código: " + linea.getCodigo());
            } else {
                LOGGER.warn("No se puede actualizar la linea, no existe con código: " + linea.getCodigo());
            }
        }
    }

    /**
     * Llamamos al metodo de borrar linea del DAO de linea, para borrar una linea existente
      * y verificamos que exista la linea y su codigo
     * @param linea linea a borrar via servicio
     */
    @Override
    public void borrarLinea(Linea linea) {
        if (linea != null && linea.getCodigo() != null) {
            if (lineaDAO.buscarTodos().containsKey(linea.getCodigo())) {
                lineaDAO.borrar(linea);
                LOGGER.info("Linea borrada via servicio con código: " + linea.getCodigo());
            } else {
                LOGGER.warn("No se puede borrar la linea, no existe con código: " + linea.getCodigo());
            }
        } else {
            LOGGER.warn("Intento de borrar una linea nula o con código inválido.");
        }
    }

    /**
     * Llamamos al metodo de buscar todos las lineas del DAO de linea, para obtener un mapa con todas las lineas
     * existentes
     * @return mapa con todas las lineas existentes via servicio
     */
    @Override
    public Map<String, Linea> buscarTodosLinea() {
        return lineaDAO.buscarTodos();
    }

    //----------------Tramo-----------------------//

    /**
     *Inserta un nuevo tramo verificando que las paradas de inicio y fin existan en el sistema.
     *Esta es una validación de integridad referencial en la capa de servicio.
     * @param tramo tramo a insertar via servicio
     */
    @Override
    public void insertarTramo(Tramo tramo) {
        if (tramo != null && tramo.getInicio() != null && tramo.getFin() != null) {
            String codigo = tramo.getInicio().getCodigo() + "-" + tramo.getFin().getCodigo();

            //Ahora nos preguntamos si existen las paradas en el sistema
            Map<Integer, Parada> paradasExistentes = paradaDAO.buscarTodos();
            boolean existeInicio = paradasExistentes.containsKey(tramo.getInicio().getCodigo());
            boolean existeFin = paradasExistentes.containsKey(tramo.getFin().getCodigo());

            if (existeInicio && existeFin) {
                //Validamos que no existan duplicados
                if (!tramoDAO.buscarTodos().containsKey(codigo)) {
                    tramoDAO.insertar(tramo);
                    LOGGER.info("Tramo insertado via servicio: " + codigo);
                } else {
                    LOGGER.warn("No se puede insertar el tramo, ya existe con código: " + codigo);
                }
            } else {
                LOGGER.error("No se puede insertar el tramo. Una o ambas paradas no existen en el sistema.");
            }
        }
    }

    /**
     *Actualiza un tramo existente verificando su presencia en el mapa de datos.
     * @param tramo tramo a actualizar via servicio
     */
    @Override
    public void actualizarTramo(Tramo tramo) {
        if (tramo != null && tramo.getInicio() != null && tramo.getFin() != null) {
            String codigo = tramo.getInicio().getCodigo() + "-" + tramo.getFin().getCodigo();
            if (tramoDAO.buscarTodos().containsKey(codigo)) {
                tramoDAO.actualizar(tramo);
                LOGGER.info("Tramo actualizado via servicio con código: " + codigo);
            } else {
                LOGGER.warn("No se puede actualizar el tramo, no existe con código: " + codigo);
            }
        }
    }

    /**
     * Elimina un tramo existente verificando su presencia en el mapa de datos.
     * @param tramo tramo a borrar via servicio
     */
    @Override
    public void borrarTramo(Tramo tramo) {
        if (tramo != null && tramo.getInicio() != null && tramo.getFin() != null) {
            String codigo = tramo.getInicio().getCodigo() + "-" + tramo.getFin().getCodigo();
            if (tramoDAO.buscarTodos().containsKey(codigo)) {
                tramoDAO.borrar(tramo);
                LOGGER.info("Tramo borrado via servicio con código: " + codigo);
            } else {
                LOGGER.warn("No se puede borrar el tramo, no existe con código: " + codigo);
            }
        }
    }

    /**
     * Llamamos al metodo de buscar todos los tramos del DAO de tramo, para obtener un mapa con todas los tramos
     * existentes
     * @return mapa con todas los tramos existentes via servicio
     */
    @Override
    public Map<String, Tramo> buscarTodosTramo() {
        return tramoDAO.buscarTodos();
    }
}