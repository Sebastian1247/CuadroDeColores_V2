/**
 * Las cuatro direcciones en las que se puede mover una ficha.
 * Cada dirección guarda cuánto cambian la fila y la columna al moverse.
 * También indica el lado del tablero en el que está una salida.
 */
public enum Direccion {
    /** Hacia arriba: la fila disminuye en 1. */
    ARRIBA(-1, 0),
    /** Hacia abajo: la fila aumenta en 1. */
    ABAJO(1, 0),
    /** Hacia la izquierda: la columna disminuye en 1. */
    IZQUIERDA(0, -1),
    /** Hacia la derecha: la columna aumenta en 1. */
    DERECHA(0, 1);

    /** Cambio en la fila al moverse en esta dirección. */
    private final int dFila;
    /** Cambio en la columna al moverse en esta dirección. */
    private final int dColumna;

    /**
     * Crea una dirección con su desplazamiento.
     *
     * @param dFila    cambio en la fila al moverse en esta dirección
     * @param dColumna cambio en la columna al moverse en esta dirección
     */
    Direccion(int dFila, int dColumna){
        this.dFila = dFila;
        this.dColumna = dColumna;
    }

    /**
     * Indica si la dirección es vertical.
     *
     * @return {@code true} para ARRIBA y ABAJO; {@code false} para IZQUIERDA y DERECHA
     */
    public boolean esVertical(){
        return this == ARRIBA || this == ABAJO;
    }
    
    /**
     * Devuelve el cambio en la fila al moverse en esta dirección.
     *
     * @return -1, 0 o 1
     */
    public int getDFila(){
        return dFila;
    }

    /**
     * Devuelve el cambio en la columna al moverse en esta dirección.
     *
     * @return -1, 0 o 1
     */
    public int getDColumna(){
        return dColumna;
    }

    /**
     * Devuelve el nombre de la dirección en minúsculas.
     *
     * @return nombre de la dirección, por ejemplo "abajo"
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
