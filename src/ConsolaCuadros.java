import java.util.List;

/**
 * Vista en consola del juego y única clase que escribe en pantalla.
 * Dibuja el tablero con colores ANSI y muestra la solución y el resumen.
 */
public class ConsolaCuadros {

    /** Código ANSI que restablece el color de la consola. */
    private static final String RESET = "\u001B[0m";
    /** Línea que enmarca el título y el resumen. */
    private static final String LINEA_DOBLE = "==================================";
    /** Línea que separa las secciones. */
    private static final String LINEA_SIMPLE = "----------------------------------";

    /** Muestra el encabezado del programa. */
    public void mostrarTitulo() {
        System.out.println(LINEA_DOBLE);
        System.out.println("        CUADROS DE COLORES");
        System.out.println("          Version basica");
        System.out.println(LINEA_DOBLE);
    }

    /**
     * Dibuja el tablero con sus fichas y con las salidas sobre el borde.
     *
     * @param tablero tablero a dibujar
     */
    public void mostrarTablero(Tablero tablero) {
        StringBuilder sb = new StringBuilder();

        sb.append(construirBordeHorizontal(tablero, Direccion.ARRIBA)).append('\n');

        for (int f = 0; f < tablero.getFilas(); f++) {
            sb.append(construirBorde(tablero, Direccion.IZQUIERDA, f, "|"));
            for (int c = 0; c < tablero.getColumnas(); c++) {
                sb.append(construirCasilla(tablero.getFicha(new Posicion(f, c))));
            }
            sb.append(construirBorde(tablero, Direccion.DERECHA, f, "|")).append('\n');
        }

        sb.append(construirBordeHorizontal(tablero, Direccion.ABAJO));
        System.out.println(sb);
    }

    /**
     * Muestra el tablero inicial y, para cada movimiento, su descripción y el
     * tablero resultante.
     *
     * @param camino camino de nodos devuelto por la búsqueda, o {@code null} si no hay solución
     */
    public void mostrarSolucion(List<Nodo> camino) {
        if (camino == null) {
            System.out.println("No hay solucion.");
        } else {
            System.out.println("\nTablero inicial:");
            mostrarTablero(camino.get(0).estado());

            System.out.println("\n" + LINEA_SIMPLE);
            System.out.println("            SOLUCION");
            System.out.println(LINEA_SIMPLE);

            for (int i = 1; i < camino.size(); i++) {
                Nodo paso = camino.get(i);
                System.out.println("\n" + i + ") " + describirMovimiento(camino.get(i - 1).estado(), paso.movimiento()));
                mostrarTablero(paso.estado());
            }
        }
    }

    /**
     * Muestra el resumen de la partida: tamaño, movimientos mínimos, estados
     * explorados, tiempo de búsqueda, salidas y fichas por color.
     *
     * @param inicial           tablero inicial
     * @param camino            camino de la solución, o {@code null} si no hay solución
     * @param estadosExplorados cantidad de estados que exploró la búsqueda
     * @param nanosegundos      duración de la búsqueda en nanosegundos
     */
    public void mostrarResumen(Tablero inicial, List<Nodo> camino, int estadosExplorados, long nanosegundos) {
        String movimientos = "sin solucion";
        if (camino != null) {
            movimientos = String.valueOf(camino.size() - 1);
        }

        StringBuilder sb = new StringBuilder();
        sb.append('\n').append(LINEA_DOBLE).append('\n');
        sb.append("             RESUMEN\n");
        sb.append(LINEA_DOBLE).append('\n');

        sb.append(String.format(" %-22s%d x %d%n", "Tablero", inicial.getFilas(), inicial.getColumnas()));
        sb.append(String.format(" %-22s%s%n", "Movimientos minimos", movimientos));
        sb.append(String.format(" %-22s%d%n", "Estados explorados", estadosExplorados));
        sb.append(String.format(" %-22s%.3f ms%n", "Tiempo de busqueda", nanosegundos / 1_000_000.0));

        sb.append(LINEA_SIMPLE).append('\n');
        sb.append(" Salidas\n");
        for (Salida salida : inicial.getSalidas()) {
            sb.append("   - ").append(salida).append('\n');
        }

        sb.append(LINEA_SIMPLE).append('\n');
        sb.append(" Fichas por color\n");
        for (Color color : Color.values()) {
            int cantidad = inicial.contarFichas(color);
            if (cantidad > 0) {
                sb.append(String.format("   - %-10s%d%n", color + ":", cantidad));
            }
        }

        sb.append(LINEA_DOBLE);
        System.out.println(sb);
    }

    /**
     * Construye el borde superior o inferior del tablero, con las salidas de ese lado.
     *
     * @param tablero tablero a dibujar
     * @param lado    ARRIBA o ABAJO
     * @return línea del borde
     */
    private String construirBordeHorizontal(Tablero tablero, Direccion lado) {
        StringBuilder sb = new StringBuilder("+");
        for (int c = 0; c < tablero.getColumnas(); c++) {
            sb.append('-').append(construirBorde(tablero, lado, c, "-")).append('-');
        }
        sb.append('+');
        return sb.toString();
    }

    /**
     * Construye un tramo del borde: la letra coloreada de la salida si hay una en
     * ese lugar o, si no, el carácter del borde.
     *
     * @param tablero tablero a dibujar
     * @param lado    lado del tablero
     * @param indice  fila o columna del tramo
     * @param borde   carácter a usar cuando no hay salida
     * @return texto del tramo del borde
     */
    private String construirBorde(Tablero tablero, Direccion lado, int indice, String borde) {
        String texto = borde;
        for (Salida salida : tablero.getSalidas()) {
            if (salida.lado() == lado && salida.indice() == indice) {
                texto = obtenerColorLetra(salida.color()) + salida.color().getSimbolo() + RESET;
            }
        }
        return texto;
    }

    /**
     * Construye una casilla de tres caracteres: la letra de la ficha con fondo de
     * color o, si está vacía, un punto.
     *
     * @param ficha ficha de la casilla, o {@code null} si está vacía
     * @return texto de la casilla
     */
    private String construirCasilla(Ficha ficha) {
        String texto = " . ";
        if (ficha != null) {
            texto = obtenerColorFondo(ficha.color()) + " " + ficha.color().getSimbolo() + " " + RESET;
        }
        return texto;
    }

    /**
     * Obtiene el código ANSI de fondo de un color. El negro se muestra en gris
     * oscuro para que se vea en terminales de fondo oscuro.
     *
     * @param color color de la ficha
     * @return código ANSI de fondo con letra blanca
     */
    private String obtenerColorFondo(Color color) {
        String codigo;
        switch (color) {
            case NEGRO:
                codigo = "\u001B[100;97m";   
                break;
            case ROJO:
                codigo = "\u001B[41;97m";    
                break;
            default:
                codigo = "";
                break;
        }
        return codigo;
    }

    /**
     * Obtiene el código ANSI de letra de un color, para dibujar las salidas.
     *
     * @param color color de la salida
     * @return código ANSI de letra en negritas
     */
    private String obtenerColorLetra(Color color) {
        String codigo;
        switch (color) {
            case NEGRO:
                codigo = "\u001B[1;90m";     
                break;
            case ROJO:
                codigo = "\u001B[1;91m";       
                break;
            default:
                codigo = "";
                break;
        }
        return codigo;
    }

    /**
     * Describe un movimiento con el color de la ficha, su origen, su dirección
     * y si sale del tablero. Usa el tablero anterior al movimiento, porque
     * después la ficha ya no está en su origen.
     *
     * @param antes      tablero antes del movimiento
     * @param movimiento movimiento aplicado
     * @return descripción, por ejemplo "Ficha de color rojo (1,1) -> abajo, sale del tablero"
     */
    private String describirMovimiento(Tablero antes, Movimiento movimiento) {
        Ficha ficha = antes.getFicha(movimiento.origen());
        String texto = "Ficha de color " + ficha.color() + " " + movimiento;
        if (!antes.estaDentro(movimiento.getDestino())) {
            texto = texto + ", sale del tablero";
        }
        return texto;
    }
}
