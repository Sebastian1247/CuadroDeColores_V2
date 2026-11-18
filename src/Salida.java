/**
 * Salida ubicada sobre una orilla del tablero. Solo deja salir fichas de su color.
 *
 * @param color  color de las fichas que pueden salir por ella
 * @param lado   lado del tablero en el que está la salida
 * @param indice columna (lados ARRIBA y ABAJO) o fila (lados IZQUIERDA y DERECHA) de la salida
 */
public record Salida (Color color, Direccion lado, int indice) {
    
    /**
     * Indica si una ficha alcanza esta salida al moverse desde una posición en una dirección.
     * No revisa si la posición está en la orilla; eso lo comprueba el tablero.
     *
     * @param posicion  posición de la ficha
     * @param direccion dirección en la que se mueve la ficha
     * @return {@code true} si la dirección coincide con el lado de la salida
     *         y la ficha está en su fila o columna
     */
    public boolean seAlcanzaDesde(Posicion posicion, Direccion direccion){
        boolean alcanza = false;

        if(direccion == lado){
            if(lado.esVertical()){
                alcanza = posicion.columna() == indice;
            } else {
                alcanza = posicion.fila() == indice;
            }
        }
        return alcanza;
    }

    /**
     * Indica si la ficha puede salir por esta salida.
     *
     * @param ficha ficha que intenta salir
     * @return {@code true} si la ficha es del color de la salida
     */
    public boolean acepta(Ficha ficha){
        return ficha.color() == color;
    }

    /**
     * Describe la salida con su color, su lado y su fila o columna.
     *
     * @return texto de la salida, por ejemplo "Salida de color rojo: abajo, columna 1"
     */
    @Override
    public String toString() {
        String eje = "fila";
        if (lado.esVertical()) {
            eje = "columna";
        }
        return "Salida de color " + color + ": " + lado + ", " + eje + " " + indice;
    }
}
