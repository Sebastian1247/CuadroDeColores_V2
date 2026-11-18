/**
 * Colores disponibles para las fichas y las salidas del tablero.
 * Cada color tiene una letra que lo representa al dibujar el tablero.
 */
public enum Color{
    /** Color negro, representado con la letra N. */
    NEGRO('N'),
    /** Color rojo, representado con la letra R. */
    ROJO('R');

    /** Letra que representa al color en el tablero. */
    private final char simbolo;

    /**
     * Crea un color con la letra que lo representa.
     *
     * @param simbolo letra que identifica al color en el tablero
     */
    Color(char simbolo){
        this.simbolo = simbolo;
    }

    /**
     * Devuelve la letra que representa al color.
     *
     * @return letra del color, por ejemplo 'R'
     */
    public char getSimbolo(){
        return simbolo;
    }

    /**
     * Devuelve el nombre del color en minúsculas.
     *
     * @return nombre del color, por ejemplo "rojo"
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}