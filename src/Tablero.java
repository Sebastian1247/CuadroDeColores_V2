import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Estado del juego: la distribución de las fichas y las salidas del tablero.
 * Es inmutable: los métodos que cambian el tablero devuelven uno nuevo y dejan intacto el original.
 * Es el experto en las reglas del juego, porque tiene la matriz de fichas y las salidas.
 */
public class Tablero {
    /** Matriz de fichas; {@code null} representa una casilla vacía. */
    private final Ficha[][] fichas;
    /** Salidas del tablero; son las mismas en todos los estados de una partida. */
    private final List<Salida> salidas;

    /**
     * Crea un tablero con una copia profunda de la matriz de fichas.
     *
     * @param fichas  matriz de fichas; {@code null} representa una casilla vacía
     * @param salidas salidas del tablero
     */
    public Tablero(Ficha[][] fichas, List<Salida> salidas) {
        this.fichas = copiar(fichas);
        this.salidas = List.copyOf(salidas);
    }

    /**
     * Indica si el tablero no tiene fichas. Es la condición de meta de la búsqueda.
     *
     * @return {@code true} si todas las casillas están vacías
     */
    public boolean estaVacio() {
        boolean vacio = true;
        for(int i = 0; i < getFilas() && vacio; i++){
            for(int j = 0; j < getColumnas() && vacio; j++){
                if(fichas[i][j] != null){
                    vacio = false;
                }
            }
        }
        return vacio;
    }

    /**
     * Obtiene todos los movimientos válidos del tablero: prueba las cuatro
     * direcciones de cada ficha y conserva las que cumplen las reglas.
     *
     * @return lista de movimientos válidos; vacía si no hay ninguno
     */
    public List<Movimiento> getMovimientosValidos() {
        List<Movimiento> movimientos = new ArrayList<>();
        for(int i = 0; i < getFilas(); i++) {
            for(int j = 0; j < getColumnas(); j++) {
                if(fichas[i][j] != null) {
                    for(Direccion direccion : Direccion.values()) {
                        Movimiento movimiento = new Movimiento(new Posicion(i, j), direccion);
                        if(esValido(movimiento)) {
                            movimientos.add(movimiento);
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    /**
     * Indica si un movimiento cumple las reglas del juego. Dentro del tablero, el
     * destino debe estar vacío; fuera de él, debe haber una salida alcanzable del
     * color de la ficha.
     *
     * @param movimiento movimiento a revisar
     * @return {@code true} si hay una ficha en el origen y el movimiento es válido
     */
    public boolean esValido(Movimiento movimiento) {
        boolean valido = false;
        Ficha ficha = getFicha(movimiento.origen());

        if(ficha != null) {
            Posicion destino = movimiento.getDestino();
            if(estaDentro(destino)) {
                valido = getFicha(destino) == null;
            } else {
                valido = haySalidaPara(ficha, movimiento.origen(), movimiento.direccion());
            }
        }
        return valido;
    }

    /**
     * Aplica un movimiento y devuelve el tablero resultante. Si la ficha sale del
     * tablero, simplemente no se coloca en el nuevo tablero. No valida el movimiento.
     *
     * @param movimiento movimiento válido a aplicar
     * @return nuevo tablero con el movimiento aplicado
     */
    public Tablero aplicar(Movimiento movimiento) {
        Ficha[][] nuevas = copiar(fichas);
        Posicion origen = movimiento.origen();
        Posicion destino = movimiento.getDestino();

        Ficha ficha = nuevas[origen.fila()][origen.columna()];
        nuevas[origen.fila()][origen.columna()] = null;

        if(estaDentro(destino)) {
            nuevas[destino.fila()][destino.columna()] = ficha;
        }

        return new Tablero(nuevas, salidas);
    }

    /**
     * Indica si todas las casillas tienen una ficha.
     *
     * @return {@code true} si no hay casillas vacías
     */
    public boolean estaLleno() {
        boolean lleno = true;
        for(int i = 0; i < getFilas() && lleno; i++){
            for(int j = 0; j < getColumnas() && lleno; j++){
                if(fichas[i][j] == null){
                    lleno = false;
                }
            }
        }
        return lleno;
    }

    /**
     * Coloca una ficha en una casilla y devuelve el tablero resultante.
     *
     * @param posicion casilla en la que se coloca la ficha
     * @param ficha    ficha a colocar
     * @return nuevo tablero con la ficha colocada
     */
    public Tablero colocar(Posicion posicion, Ficha ficha) {
        Ficha[][] nuevas = copiar(fichas);
        nuevas[posicion.fila()][posicion.columna()] = ficha;
        return new Tablero(nuevas, salidas);
    }

    /**
     * Cuenta las fichas de un color.
     *
     * @param color color a contar
     * @return cantidad de fichas de ese color en el tablero
     */
    public int contarFichas(Color color) {
        int cantidad = 0;
        for (int i = 0; i < getFilas(); i++) {
            for (int j = 0; j < getColumnas(); j++) {
                if (fichas[i][j] != null && fichas[i][j].color() == color) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }

    /**
     * Obtiene la casilla del tablero que está pegada a una salida.
     *
     * @param salida salida del tablero
     * @return posición de la casilla adyacente a la salida
     */
    public Posicion getCasillaAdyacente(Salida salida) {
        Posicion casilla;
        if(salida.lado() == Direccion.ARRIBA) {
            casilla = new Posicion(0, salida.indice());
        } else if(salida.lado() == Direccion.ABAJO) {
            casilla = new Posicion(getFilas() - 1, salida.indice());
        } else if(salida.lado() == Direccion.IZQUIERDA) {
            casilla = new Posicion(salida.indice(), 0);
        } else { 
            casilla = new Posicion(salida.indice(), getColumnas() - 1);
        }
        return casilla;
    }

    /**
     * Devuelve el número de filas del tablero.
     *
     * @return cantidad de filas
     */
    public int getFilas() {
        return fichas.length;
    }
    
    /**
     * Devuelve el número de columnas del tablero.
     *
     * @return cantidad de columnas
     */
    public int getColumnas() {
        return fichas[0].length;
    }

    /**
     * Devuelve las salidas del tablero.
     *
     * @return lista no modificable de salidas
     */
    public List<Salida> getSalidas() {
        return salidas;
    }

    /**
     * Indica si una posición está dentro de los límites del tablero.
     *
     * @param posicion posición a revisar
     * @return {@code true} si la fila y la columna están dentro del tablero
     */
    public boolean estaDentro(Posicion posicion) {
        return posicion.fila() >= 0 && posicion.fila() < getFilas() &&
               posicion.columna() >= 0 && posicion.columna() < getColumnas();
    }

    /**
     * Obtiene la ficha de una casilla.
     *
     * @param posicion casilla a consultar
     * @return la ficha de la casilla, o {@code null} si está vacía o fuera del tablero
     */
    public Ficha getFicha(Posicion posicion) {
        Ficha ficha = null;
        if(estaDentro(posicion)){
            ficha = fichas[posicion.fila()][posicion.columna()];
        }
        return ficha;
    }

    /**
     * Busca una salida que se alcance desde el origen en la dirección indicada
     * y que acepte la ficha.
     *
     * @param ficha     ficha que intenta salir
     * @param origen    posición de la ficha
     * @param direccion dirección del movimiento
     * @return {@code true} si existe una salida por la que la ficha puede salir
     */
    private boolean haySalidaPara(Ficha ficha, Posicion origen, Direccion direccion) {
        boolean encontrada = false;
        for(int i = 0; i < salidas.size() && !encontrada; i++){
            Salida salida = salidas.get(i);
            if(salida.seAlcanzaDesde(origen, direccion) && salida.acepta(ficha)) {
                encontrada = true;
            }
        }
        return encontrada;
    }

    /**
     * Hace una copia profunda de una matriz de fichas: copia cada fila, no solo
     * el arreglo exterior. Las fichas se comparten porque son inmutables.
     *
     * @param original matriz a copiar
     * @return copia independiente de la matriz
     */
    private static Ficha[][] copiar(Ficha[][] original) {
        Ficha[][] copia = new Ficha[original.length][];
        for(int i = 0; i < original.length; i++){
            copia[i] = original[i].clone();
        }
        return copia;
    }

    /**
     * Compara dos tableros por el contenido de su matriz de fichas.
     * Las salidas no se comparan porque son las mismas en todos los estados de una partida.
     *
     * @param obj objeto a comparar
     * @return {@code true} si el otro objeto es un tablero con las mismas fichas en las mismas casillas
     */
    @Override 
    public boolean equals(Object obj){
        boolean iguales = false;
        if(this == obj){
            iguales = true;
        } else if(obj instanceof Tablero otro) {
            iguales = Arrays.deepEquals(fichas, otro.fichas);
        }
        return iguales;
    }

    /**
     * Calcula el código hash a partir del contenido de la matriz de fichas,
     * de forma consistente con {@link #equals(Object)}.
     *
     * @return código hash del tablero
     */
    @Override 
    public int hashCode(){
        return Arrays.deepHashCode(fichas);
    }

}
