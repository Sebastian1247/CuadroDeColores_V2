import java.util.List;

/**
 * Coordina una partida: genera el tablero, busca la solución midiendo el tiempo
 * y pide a la consola que muestre los resultados. No contiene reglas del juego ni textos.
 */
public class ControladorJuego {

    /** Genera el tablero inicial. */
    private final GeneradorTablero generador;
    /** Busca la secuencia mínima de movimientos. */
    private final BuscadorBFS buscador;
    /** Muestra el juego en la consola. */
    private final ConsolaCuadros consola;

    /** Crea el controlador de la versión básica: tablero de 2x2 con dos colores. */
    public ControladorJuego() {
        this(new GeneradorTablero(), new BuscadorBFS(), new ConsolaCuadros());
    }

    /**
     * Crea el controlador con las piezas indicadas, por ejemplo un generador
     * con semilla o de otro tamaño.
     *
     * @param generador generador de tableros
     * @param buscador  buscador de la solución
     * @param consola   vista en consola
     */
    public ControladorJuego(GeneradorTablero generador, BuscadorBFS buscador, ConsolaCuadros consola) {
        this.generador = generador;
        this.buscador = buscador;
        this.consola = consola;
    }

    /**
     * Juega una partida: muestra el título, genera el tablero, busca la
     * solución mínima midiendo solo el tiempo de la búsqueda, y muestra la
     * solución y el resumen.
     */
    public void jugar() {
        consola.mostrarTitulo();
        Tablero tablero = generador.generar();

        long inicio = System.nanoTime();
        List<Nodo> camino = buscador.buscar(tablero);
        long duracion = System.nanoTime() - inicio;

        consola.mostrarSolucion(camino);
        consola.mostrarResumen(tablero, camino, buscador.getEstadosExplorados(), duracion);
    }
}

