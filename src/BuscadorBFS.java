import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Búsqueda en anchura (BFS) de la secuencia mínima de movimientos para vaciar el tablero.
 * Sigue el capítulo 2 de "Classic Computer Science Problems in Java" (Kopec).
 */
public class BuscadorBFS {

    /** Cantidad de estados que se sacaron de la cola en la última búsqueda. */
    private int estadosExplorados;

    /**
     * Busca la secuencia con el menor número de movimientos para vaciar el tablero.
     * Un estado se marca como explorado al entrar a la cola, y la meta se revisa al sacarlo.
     *
     * @param inicial tablero desde el que empieza la búsqueda
     * @return camino de nodos desde el tablero inicial hasta el tablero vacío,
     *         o {@code null} si no hay solución
     */
    public List<Nodo> buscar(Tablero inicial) {
        Queue<Nodo> frontera = new ArrayDeque<>();
        Set<Tablero> explorados = new HashSet<>();
        estadosExplorados = 0;

        frontera.add(new Nodo(inicial));
        explorados.add(inicial);

        Nodo meta = null;
        while (!frontera.isEmpty() && meta == null) {
            Nodo actual = frontera.poll();
            estadosExplorados++;

            if (actual.estado().estaVacio()) {
                meta = actual;
            } else {
                for (Movimiento movimiento : actual.estado().getMovimientosValidos()) {
                    Tablero siguiente = actual.estado().aplicar(movimiento);
                    if (!explorados.contains(siguiente)) {
                        explorados.add(siguiente);
                        frontera.add(new Nodo(siguiente, actual, movimiento));
                    }
                }
            }
        }

        List<Nodo> camino = null;
        if (meta != null) {
            camino = reconstruirCamino(meta);
        }
        return camino;
    }

    /**
     * Reconstruye el camino siguiendo los padres desde la meta hasta el nodo
     * inicial, y lo invierte.
     *
     * @param meta nodo con el tablero vacío
     * @return camino ordenado desde el nodo inicial hasta la meta
     */
    private List<Nodo> reconstruirCamino(Nodo meta) {
        List<Nodo> camino = new ArrayList<>();
        Nodo actual = meta;
        while (actual != null) {
            camino.add(actual);
            actual = actual.padre();
        }
        Collections.reverse(camino);
        return camino;
    }

    /**
     * Devuelve cuántos estados se sacaron de la cola en la última búsqueda.
     *
     * @return cantidad de estados explorados
     */
    public int getEstadosExplorados() {
        return estadosExplorados;
    }
}
