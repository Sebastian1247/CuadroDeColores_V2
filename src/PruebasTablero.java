import java.util.List;
import java.util.Random;

/** Pruebas manuales del modelo, el buscador y el generador. */
public class PruebasTablero {

    private static final ConsolaCuadros consola = new ConsolaCuadros();

    public static void main(String[] args) {
        probarTablero();
        probarBuscador();
        probarGenerador();
    }

    // ---------- Tablero ----------

    private static void probarTablero() {
        System.out.println("=== Pruebas de Tablero ===");
        Tablero inicial = crearTableroEjemplo();
        consola.mostrarTablero(inicial);

        // 1. Movimientos validos: solo la negra de (0,0) puede salir por arriba
        List<Movimiento> movs = inicial.getMovimientosValidos();
        System.out.println("Movimientos validos: " + movs);
        verificar("Solo hay 1 movimiento valido", movs.size() == 1);
        verificar("Es (0,0) -> ARRIBA",
                movs.contains(new Movimiento(new Posicion(0, 0), Direccion.ARRIBA)));

        // 2. Ficha frente a salida de otro color NO puede salir
        Movimiento negraPorSalidaRoja = new Movimiento(new Posicion(1, 1), Direccion.ABAJO);
        verificar("Negra no sale por salida roja", !inicial.esValido(negraPorSalidaRoja));

        // 3. aplicar() no modifica el original
        Tablero siguiente = inicial.aplicar(movs.get(0));
        System.out.println("\nDespues de " + movs.get(0) + ":");
        consola.mostrarTablero(siguiente);
        verificar("El original sigue teniendo la ficha en (0,0)",
                inicial.getFicha(new Posicion(0, 0)) != null);
        verificar("El nuevo tiene (0,0) vacia",
                siguiente.getFicha(new Posicion(0, 0)) == null);

        // Ahora la (0,0) esta libre: la roja de (0,1) puede ir a la IZQUIERDA
        // y la roja de (1,0) puede ir ARRIBA
        List<Movimiento> movs2 = siguiente.getMovimientosValidos();
        System.out.println("Movimientos validos: " + movs2);
        verificar("Hay 2 movimientos validos", movs2.size() == 2);

        // 4. equals y hashCode
        Ficha N = new Ficha(Color.NEGRO);
        Ficha R = new Ficha(Color.ROJO);
        Ficha[][] mismaMatriz = {
            { null, R },
            { R,    N }
        };
        Tablero igual = new Tablero(mismaMatriz, inicial.getSalidas());
        verificar("equals entre tableros iguales", siguiente.equals(igual));
        verificar("hashCode igual", siguiente.hashCode() == igual.hashCode());
        verificar("equals con tablero distinto es false", !inicial.equals(siguiente));

        // 5. Modificar la matriz original no afecta al tablero (copia profunda)
        Ficha[][] matriz = {
            { N, R },
            { R, N }
        };
        Tablero protegido = new Tablero(matriz, inicial.getSalidas());
        matriz[1][1] = null;
        verificar("La copia protege al tablero",
                protegido.getFicha(new Posicion(1, 1)) != null);

        // 6. Vacio y lleno
        Tablero vacio = new Tablero(new Ficha[2][2], inicial.getSalidas());
        verificar("estaVacio() en tablero vacio", vacio.estaVacio());
        verificar("estaVacio() en tablero con fichas es false", !inicial.estaVacio());
        verificar("estaLleno() en tablero lleno", inicial.estaLleno());
        verificar("estaLleno() en tablero vacio es false", !vacio.estaLleno());
    }

    // ---------- BuscadorBFS ----------

    private static void probarBuscador() {
        System.out.println("\n=== Pruebas de BuscadorBFS ===");
        Tablero inicial = crearTableroEjemplo();

        // 7. Solucion minima del ejemplo del PDF
        List<Nodo> camino = new BuscadorBFS().buscar(inicial);
        verificar("Encontro solucion", camino != null);
        if (camino != null) {
            verificar("Son 10 movimientos (como en el PDF)", camino.size() - 1 == 10);
            verificar("El ultimo tablero esta vacio",
                    camino.get(camino.size() - 1).estado().estaVacio());
        }
        consola.mostrarSolucion(camino);

        // 8. Tablero ya vacio: camino con solo el nodo inicial (0 movimientos)
        Tablero vacio = new Tablero(new Ficha[2][2], inicial.getSalidas());
        List<Nodo> caminoVacio = new BuscadorBFS().buscar(vacio);
        verificar("Tablero vacio se resuelve con 0 movimientos",
                caminoVacio != null && caminoVacio.size() == 1);
    }

    // ---------- GeneradorTablero ----------

    private static void probarGenerador() {
        System.out.println("\n=== Pruebas de GeneradorTablero ===");
        GeneradorTablero generador =
                new GeneradorTablero(2, 2, List.of(Color.NEGRO, Color.ROJO), new Random(42));

        // 9. Varios tableros generados: todos llenos y con solucion
        for (int k = 0; k < 3; k++) {
            Tablero generado = generador.generar();
            consola.mostrarTablero(generado);
            List<Nodo> sol = new BuscadorBFS().buscar(generado);
            verificar("Tiene solucion", sol != null);
            verificar("Esta lleno", generado.estaLleno());
            if (sol != null) {
                System.out.println("Movimientos minimos: " + (sol.size() - 1) + "\n");
            }
        }
    }

    // ---------- Auxiliares ----------

    /** Tablero del ejemplo del PDF: N R / R N, salida negra arriba-col0, roja abajo-col1. */
    private static Tablero crearTableroEjemplo() {
        Ficha N = new Ficha(Color.NEGRO);
        Ficha R = new Ficha(Color.ROJO);
        Ficha[][] matriz = {
            { N, R },
            { R, N }
        };
        List<Salida> salidas = List.of(
            new Salida(Color.NEGRO, Direccion.ARRIBA, 0),
            new Salida(Color.ROJO, Direccion.ABAJO, 1)
        );
        return new Tablero(matriz, salidas);
    }

    private static void verificar(String descripcion, boolean condicion) {
        String resultado = "FALLA";
        if (condicion) {
            resultado = "OK";
        }
        System.out.println("[" + resultado + "] " + descripcion);
    }
}

