/**
 * Paso en el árbol de búsqueda: un estado del tablero junto con el nodo del que
 * proviene y el movimiento que lo produjo.
 *
 * @param estado     tablero en este paso
 * @param padre      nodo anterior; {@code null} en el nodo inicial
 * @param movimiento movimiento que llevó del padre a este estado; {@code null} en el nodo inicial
 */
public record Nodo(Tablero estado, Nodo padre, Movimiento movimiento) {
    
    /**
     * Crea el nodo inicial de una búsqueda, sin padre ni movimiento.
     *
     * @param estado tablero inicial
     */
    public Nodo(Tablero estado) {
        this(estado, null, null);
    }
}
