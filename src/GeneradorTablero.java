import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera tableros llenos y con solución garantizada jugando al revés: parte del
 * tablero vacío, mete fichas por las salidas de su color y las mezcla con
 * movimientos válidos. Como todos los movimientos son reversibles, invertir esa
 * secuencia vacía el tablero.
 */
public class GeneradorTablero {

    /** Número de filas de la versión básica. */
    private static final int FILAS_BASICA = 2;
    /** Número de columnas de la versión básica. */
    private static final int COLUMNAS_BASICA = 2;
    /** Colores de la versión básica: negro y rojo. */
    private static final List<Color> COLORES_BASICA = List.of(Color.NEGRO, Color.ROJO);

    /** Número de filas de los tableros generados. */
    private final int filas;
    /** Número de columnas de los tableros generados. */
    private final int columnas;
    /** Colores de las fichas; hay una salida por cada color. */
    private final List<Color> colores;
    /** Generador de números aleatorios. */
    private final Random random;

    /** Crea un generador para la versión básica: tablero de 2x2 con fichas negras y rojas. */
    public GeneradorTablero() {
        this(FILAS_BASICA, COLUMNAS_BASICA, COLORES_BASICA, new Random());
    }

    /**
     * Crea un generador con el tamaño, los colores y el generador aleatorio indicados.
     *
     * @param filas    número de filas del tablero
     * @param columnas número de columnas del tablero
     * @param colores  colores de las fichas; se crea una salida por cada color
     * @param random   generador de números aleatorios; con una semilla fija produce siempre los mismos tableros
     */
    public GeneradorTablero(int filas, int columnas, List<Color> colores, Random random) {
        this.filas = filas;
        this.columnas = columnas;
        this.colores = List.copyOf(colores);
        this.random = random;
    }

    /**
     * Genera un tablero lleno con solución garantizada. Crea las salidas y, hasta
     * llenar el tablero, mete una ficha por una salida libre de su color y mezcla.
     *
     * @return tablero inicial lleno y con solución
     */
    public Tablero generar() {
        Tablero tablero = new Tablero(new Ficha[filas][columnas], generarSalidas());
        int colocadas = 0;

        while (!tablero.estaLleno()) {
            Color color = elegirColor(colocadas);
            Salida salida = buscarSalidaLibre(tablero, color);

            if (salida != null) {
                tablero = tablero.colocar(tablero.getCasillaAdyacente(salida), new Ficha(color));
                colocadas++;
            }
            tablero = mezclar(tablero);
        }
        return tablero;
    }

    /**
     * Crea una salida por cada color, en un lado y un índice aleatorios, sin
     * repetir el mismo lugar.
     *
     * @return lista de salidas, una por color
     */
    private List<Salida> generarSalidas() {
        List<Salida> salidas = new ArrayList<>();
        for (Color color : colores) {
            Salida salida;
            do {
                Direccion lado = Direccion.values()[random.nextInt(Direccion.values().length)];
                int limite = columnas;
                if (!lado.esVertical()) {
                    limite = filas;
                }
                salida = new Salida(color, lado, random.nextInt(limite));
            } while (ocupaMismoLugar(salidas, salida));
            salidas.add(salida);
        }
        return salidas;
    }

    /**
     * Indica si ya existe una salida en el mismo lado e índice que la nueva.
     *
     * @param salidas salidas creadas hasta el momento
     * @param nueva   salida candidata
     * @return {@code true} si el lugar ya está ocupado por otra salida
     */
    private boolean ocupaMismoLugar(List<Salida> salidas, Salida nueva) {
        boolean repetida = false;
        for (int i = 0; i < salidas.size() && !repetida; i++) {
            Salida s = salidas.get(i);
            if (s.lado() == nueva.lado() && s.indice() == nueva.indice()) {
                repetida = true;
            }
        }
        return repetida;
    }

    /**
     * Elige el color de la siguiente ficha. Las primeras toman cada color una vez,
     * para garantizar que aparezcan todos; las demás se eligen al azar.
     *
     * @param colocadas cantidad de fichas colocadas hasta el momento
     * @return color de la siguiente ficha
     */
    private Color elegirColor(int colocadas) {
        Color color;
        if (colocadas < colores.size()) {
            color = colores.get(colocadas);
        } else {
            color = colores.get(random.nextInt(colores.size()));
        }
        return color;
    }

    /**
     * Busca una salida del color indicado cuya casilla adyacente esté vacía.
     *
     * @param tablero tablero actual
     * @param color   color de la ficha que va a entrar
     * @return salida libre de ese color, o {@code null} si todas están bloqueadas
     */
    private Salida buscarSalidaLibre(Tablero tablero, Color color) {
        Salida libre = null;
        List<Salida> salidas = tablero.getSalidas();
        for (int i = 0; i < salidas.size() && libre == null; i++) {
            Salida s = salidas.get(i);
            if (s.color() == color && tablero.getFicha(tablero.getCasillaAdyacente(s)) == null) {
                libre = s;
            }
        }
        return libre;
    }

    /**
     * Aplica entre 1 y (número de casillas) movimientos aleatorios dentro del
     * tablero, sin sacar fichas, para que no queden siempre junto a las salidas.
     *
     * @param tablero tablero a mezclar
     * @return tablero después de la mezcla
     */
    private Tablero mezclar(Tablero tablero) {
        Tablero actual = tablero;
        int casillas = tablero.getFilas() * tablero.getColumnas();
        int pasos = 1 + random.nextInt(casillas);

        for (int i = 0; i < pasos; i++) {
            List<Movimiento> internos = new ArrayList<>();
            for (Movimiento m : actual.getMovimientosValidos()) {
                if (actual.estaDentro(m.getDestino())) {
                    internos.add(m);
                }
            }
            if (!internos.isEmpty()) {
                actual = actual.aplicar(internos.get(random.nextInt(internos.size())));
            }
        }
        return actual;
    }
}

