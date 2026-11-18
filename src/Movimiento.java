/**
 * Movimiento de la ficha que está en una posición, una casilla en una dirección.
 *
 * @param origen    posición de la ficha que se mueve
 * @param direccion dirección del movimiento
 */
public record Movimiento(Posicion origen, Direccion direccion) {

    /**
     * Calcula la posición a la que llegaría la ficha.
     *
     * @return posición destino; queda fuera del tablero si la ficha sale
     */
    public Posicion getDestino(){
        return origen.mover(direccion);
    }

    /**
     * Describe el movimiento con su origen y su dirección.
     *
     * @return texto del movimiento, por ejemplo "(1,1) -> abajo"
     */
    @Override
    public String toString() {
        return origen + " -> " + direccion;
    }

}