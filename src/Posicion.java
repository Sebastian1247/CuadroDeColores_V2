/**
 * Casilla del tablero identificada por su fila y su columna, empezando en 0.
 *
 * @param fila    fila de la casilla
 * @param columna columna de la casilla
 */
public record Posicion(int fila, int columna) {
    /**
     * Calcula la posición vecina en la dirección indicada.
     * No revisa los límites, porque la posición no conoce el tamaño del tablero.
     *
     * @param direccion dirección hacia la que se mueve
     * @return posición vecina; puede quedar fuera del tablero
     */
    public Posicion mover(Direccion direccion){
        return new Posicion(fila + direccion.getDFila(), 
                            columna + direccion.getDColumna());
    }

    /**
     * Devuelve la posición en el formato (fila,columna).
     *
     * @return texto de la posición, por ejemplo "(0,1)"
     */
    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}
