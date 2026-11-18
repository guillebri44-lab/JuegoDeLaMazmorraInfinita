import java.util.Scanner;

public class Juegodelamazmorra {
    static Scanner sc = new Scanner(System.in);
    
    // Dimensiones del tablero
    
    static final int FILAS = 10;
    static final int COLUMNAS = 20;
    
    // Caracteres
    
    static  final char PUNTO = '.';
    static  final char JUGADOR = 'P';
    static  final char TRAMPA = 'X'; // sigue siendo X en la matriz pero no se ca a mostrar
    static  final char ENEMIGO = 'E';
    static final char COMERCIANTE = '$';
    static final char COFRE = 'C';
    static final char LLAVE_1 = '1', LLAVE_2 = '2', LLAVE_3 = '3';
    static final char MONEDA_EASTER_EGG = 'o';
    
    // Matriz del tablero
    
    static char [][] tablero = new  char[FILAS][COLUMNAS];
    
    // Posicion del jugador
    static int filaJugador = 0;
    static int colJugador = 0;
    static char casillaJugador = PUNTO;
    
    // Variables del juego
    static  int vidas = 5;
    static int CANTIDAD_TRAMPAS = 5;
    static int CANTIDAD_ENEMIGOS = 15;
    static int oro = 0;
    static int llaveCorrecta;
    static int llaveEnMano = 0;
    static int monedasEasterEgg = 0;
    static boolean tieneLlaveCorrecta = false;
    static boolean victoria = false;
    
    public static void main(String[] args) {
        boolean jugarOtra = true;
        while (jugarOtra) {
            System.out.println("\n=== LA MAZMORRA INFINITA ===");
            mostrarInstrucciones();
            System.out.println("Elige dificultad: 1. Facil  2. Normal  3. Dificil");
            int dificultad = leerOpcion("Dificultad: ", 1, 3);
            if (dificultad == 1) { CANTIDAD_TRAMPAS = 0; CANTIDAD_ENEMIGOS = 8; }
            else if (dificultad == 2) { CANTIDAD_TRAMPAS = 0; CANTIDAD_ENEMIGOS = 15; }
            else { CANTIDAD_TRAMPAS = 0; CANTIDAD_ENEMIGOS = 20; }

            vidas = 5;
            oro = 0;
            llaveCorrecta = (int)(Math.random() * 3) + 1;
            tieneLlaveCorrecta = false;
            llaveEnMano = 0;
            monedasEasterEgg = 0;
            victoria = false;
            inicializarTablero();
            colocarElementos();
            System.out.println("Iniciando una nueva partida con el mapa generado al azar...");

            boolean salir = false;
            while (!salir && vidas > 0 && !victoria) {
                mostrarTablero();
                System.out.println("Mover: W S A D | F (interactuar) | Q (salir)");
                String opcion = sc.nextLine().trim().toLowerCase();
                switch (opcion) {
                    case "w": mover(-1, 0); break;
                    case "s": mover(1, 0); break;
                    case "a": mover(0, -1); break;
                    case "d": mover(0, 1); break;
                    case "f":
                        try {
                            interactuar();
                        } catch (IllegalStateException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "q": salir = true; break;
                    default:
                        try {
                            validarTecla(opcion);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                }
            }

            if (victoria && monedasEasterEgg == 3)
                System.out.println("¡Victoria! Eres el Rey de la Mazmorra.");
            else if (victoria) System.out.println("¡Victoria! Abriste el cofre y completaste la mazmorra.");
            else if (vidas <= 0) {
                System.out.println("GAME OVER: te has quedado sin vidas.");
                mostrarTableroFinal();
            } else System.out.println("Has abandonado la partida.");

            jugarOtra = leerOpcion("¿Quieres volver a jugar? 1. Si  2. No: ", 1, 2) == 1;
        }
        System.out.println("Fin del juego.");
        sc.close();
    }

    static void mostrarInstrucciones() {
        System.out.println("+--------------------------------------------------+");
        System.out.println("|                  COMO JUGAR                     |");
        System.out.println("+--------------------------------------------------+");
        System.out.println("TABLERO");
        System.out.println("  P       Tu personaje");
        System.out.println("  .       Casilla vacia");
        System.out.println("  E       Monstruo (aparece al encontrarlo)");
        System.out.println("  $       Comerciante");
        System.out.println("  C       Cofre del tesoro");
        System.out.println("  1, 2, 3 Llaves visibles; solo una abre el cofre");
        System.out.println("  Monedas Easter egg ocultas por el mapa");
        System.out.println();
        System.out.println("CONTROLES");
        System.out.println("  W  Mover arriba       S  Mover abajo");
        System.out.println("  A  Mover izquierda   D  Mover derecha");
        System.out.println("  F  Recoger llave o moneda / probar llave en el cofre");
        System.out.println("  Q  Salir de la partida");
        System.out.println();
        System.out.println("OBJETIVO Y EVENTOS");
        System.out.println("  Encuentra llaves y pruébalas en el cofre con F para ganar la mazmorra.");
        System.out.println("  Encuentra las 3 monedas y abre el cofre para ser Rey de la Mazmorra.");
        System.out.println("+--------------------------------------------------+");
        System.out.println();
    }

    static int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            int opcion;
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Solo se pueden introducir numeros.");
                continue;
            }
            try {
                validarOpcionMenu(opcion, minimo, maximo);
                return opcion;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static void validarOpcionMenu(int opcion, int minimo, int maximo) {
        if (opcion < minimo || opcion > maximo)
            throw new IllegalArgumentException("Opcion inexistente. Elige un numero entre " + minimo + " y " + maximo + ".");
    }

    static void validarTecla(String opcion) {
        if (!opcion.matches("[a-zA-Z]+"))
            throw new IllegalArgumentException("Solo se pueden utilizar letras para los controles.");
        throw new IllegalArgumentException("Movimiento invalido. Usa W, A, S, D, F o Q.");
    }
    
    //Llena el tablero de puntos y coloca al jugador en el centro
        static void inicializarTablero(){
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    tablero[i][j] = PUNTO;
                }
            }
            filaJugador = FILAS / 2;
            colJugador = COLUMNAS / 2;
            casillaJugador = PUNTO;
            tablero[filaJugador][colJugador] = JUGADOR;
        }
    
        //Coloca las 5 trampas y los 4 enemigos
        static void colocarElementos(){
            int trampasColocadas = 0;
            int enemigosColocados = 0;
            
            while (trampasColocadas < CANTIDAD_TRAMPAS || enemigosColocados < CANTIDAD_ENEMIGOS) {                
                int f = (int)(Math.random()*FILAS);
                int c = (int)(Math.random()*COLUMNAS);
                
                //colocar trampa y enemigo si la celda está vacia
                if (tablero[f][c] == PUNTO) {
                    if (trampasColocadas < CANTIDAD_TRAMPAS) {
                        tablero[f][c] = TRAMPA;
                        trampasColocadas++;
                    } else if (enemigosColocados < CANTIDAD_ENEMIGOS){
                        tablero[f][c] = ENEMIGO;
                        enemigosColocados++;
                    }
                     
                    
                }
                        
            }
            int comerciantesColocados = 0;
            while (comerciantesColocados < 4) {
                int f = (int)(Math.random() * FILAS);
                int c = (int)(Math.random() * COLUMNAS);
                if (tablero[f][c] == PUNTO) {
                    tablero[f][c] = COMERCIANTE;
                    comerciantesColocados++;
                }
            }
            colocarCofreYLlaves();
            colocarMonedasEasterEgg();
        }

        // Las monedas se colocan en casillas libres y se ocultan durante la partida.
        static void colocarMonedasEasterEgg() {
            int colocadas = 0;
            while (colocadas < 3) {
                int f = (int)(Math.random() * FILAS);
                int c = (int)(Math.random() * COLUMNAS);
                if (tablero[f][c] == PUNTO) {
                    tablero[f][c] = MONEDA_EASTER_EGG;
                    colocadas++;
                }
            }
        }

        // Coloca el cofre y tres llaves visibles en puntos separados del mapa.
        static void colocarCofreYLlaves() {
            int cofreFila, cofreCol;
            do {
                cofreFila = (int)(Math.random() * FILAS);
                cofreCol = (int)(Math.random() * COLUMNAS);
            } while (tablero[cofreFila][cofreCol] != PUNTO);
            tablero[cofreFila][cofreCol] = COFRE;

            char[] llaves = {LLAVE_1, LLAVE_2, LLAVE_3};
            int[] filas = new int[3];
            int[] columnas = new int[3];
            for (int i = 0; i < llaves.length; i++) {
                boolean separada;
                do {
                    filas[i] = (int)(Math.random() * FILAS);
                    columnas[i] = (int)(Math.random() * COLUMNAS);
                    separada = tablero[filas[i]][columnas[i]] == PUNTO;
                    for (int j = 0; j < i && separada; j++) {
                        separada = Math.abs(filas[i] - filas[j]) + Math.abs(columnas[i] - columnas[j]) >= 5;
                    }
                } while (!separada);
                tablero[filas[i]][columnas[i]] = llaves[i];
            }
        }
        
     // Imprime la matriz en consola junto con el contador de vidas
        static void mostrarTablero(){
            System.out.println("\n --- VIDAS: " + vidas + "---");
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                        if (tablero[i][j] == TRAMPA || tablero[i][j] == ENEMIGO
                                || tablero[i][j] == MONEDA_EASTER_EGG) {
                        System.out.print(PUNTO + " ");
                    } else {
                            System.out.print(tablero[i][j] + " ");   
                    }
                }
                System.out.println();
            }
            System.out.println("Leyenda: $ comerciante | C cofre | 1, 2 y 3 llaves | P jugador");
            if (casillaJugador == LLAVE_1 || casillaJugador == LLAVE_2 || casillaJugador == LLAVE_3)
                System.out.println("Estas sobre la llave " + casillaJugador + ". Pulsa F para cogerla.");
            else if (casillaJugador == COFRE)
                System.out.println("Estas sobre el cofre. Pulsa F para intentar abrirlo.");
            else if (casillaJugador == MONEDA_EASTER_EGG)
                System.out.println("Estas sobre una moneda easter egg. Pulsa F para recogerla.");
            System.out.println();
        }
        
     //Muestra el tablero con las trampas y los enemigos (para el GAME OVER)
        
        static  void mostrarTableroFinal(){
            System.out.println("\n --- TABLERO REVELADO ---");
                for (int i = 0; i < FILAS; i++) {
                    for (int j = 0; j < COLUMNAS; j++) {
                        System.out.print(tablero[i][j] + " ");
                    }
                     System.out.println();
            }
                 System.out.println();
        }
    
    // Mueve al jugador validando limites del tablero y comprobando trampas
        static void mover(int deltaFila, int deltaCol){
            int nuevaFila = filaJugador + deltaFila;
            int nuevaCol = colJugador + deltaCol;

            try {
                validarMovimiento(nuevaFila, nuevaCol);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }

            // El jugador solo entra en la casilla del monstruo si gana el duelo.
            if (tablero[nuevaFila][nuevaCol] == ENEMIGO && !enfrentarEnemigo()) {
                return;
            }

            tablero[filaJugador][colJugador] = casillaJugador;
            char destino = tablero[nuevaFila][nuevaCol];
            if (destino == COMERCIANTE) {
                juegoComerciante();
                destino = PUNTO;
            }
            filaJugador = nuevaFila;
            colJugador = nuevaCol;
            casillaJugador = destino == ENEMIGO ? PUNTO : destino;
            tablero[filaJugador][colJugador] = JUGADOR;
        }

        static void validarMovimiento(int fila, int columna) {
            if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS)
                throw new IllegalArgumentException("Movimiento invalido: no puedes salir del tablero.");
        }

        static void recogerMonedaEasterEgg() {
            monedasEasterEgg++;
            System.out.println("¡Encontraste la moneda easter egg " + monedasEasterEgg + "!");
            String[] chistes = {
                "¿Sabes por que el tomate se puso rojo? Porque vio la ensalada desnuda.",
                "¿Que hace una abeja en el gimnasio? ¡ zum-ba!",
                "Esto era un hombre tan bajito que se sentaba en el bordillo y le colgaban los pies."
            };
            System.out.println("Chiste de cuñado: " + chistes[monedasEasterEgg - 1]);
        }

        // F recoge la llave de la casilla actual o intenta abrir el cofre.
        static void interactuar() {
            if (casillaJugador == MONEDA_EASTER_EGG) {
                recogerMonedaEasterEgg();
                casillaJugador = PUNTO;
            } else if (casillaJugador == LLAVE_1 || casillaJugador == LLAVE_2 || casillaJugador == LLAVE_3) {
                if (llaveEnMano != 0) {
                    System.out.println("Ya llevas una llave. Pruebala en el cofre antes de coger otra.");
                    return;
                }
                llaveEnMano = casillaJugador - '0';
                System.out.println("Has recogido la llave " + llaveEnMano + ". Su valor se sabra al probarla en el cofre.");
                casillaJugador = PUNTO;
            } else if (casillaJugador == COFRE) {
                if (llaveEnMano == 0) {
                    System.out.println("No llevas ninguna llave para probar en el cofre.");
                } else if (llaveEnMano == llaveCorrecta) {
                    victoria = true;
                    casillaJugador = PUNTO;
                    System.out.println("¡La llave funciona! Has abierto el cofre.");
                } else {
                    System.out.println("Esa llave no sirve. El cofre sigue cerrado; busca otra llave.");
                    llaveEnMano = 0;
                }
            } else {
                throw new IllegalStateException("No hay ningun objeto disponible en esta casilla.");
            }
        }

        // El comerciante sortea dos numeros distintos y premia si el jugador acierta uno.
        static void juegoComerciante() {
            int elegido = 0;
            while (elegido < 1 || elegido > 5) {
                System.out.print("Comerciante: elige un numero del 1 al 5: ");
                try {
                    elegido = Integer.parseInt(sc.nextLine().trim());
                    if (elegido < 1 || elegido > 5) System.out.println("El numero debe estar entre 1 y 5.");
                } catch (NumberFormatException e) {
                    System.out.println("Solo se pueden introducir numeros.");
                }
            }
            int numero1 = (int)(Math.random() * 5) + 1;
            int numero2;
            do { numero2 = (int)(Math.random() * 5) + 1; } while (numero2 == numero1);
            System.out.println("El comerciante saco " + numero1 + " y " + numero2 + ".");
            if (elegido == numero1 || elegido == numero2) {
                if (vidas < 5) { vidas++; System.out.println("¡Acertaste! Ganas una vida."); }
                else System.out.println("¡Acertaste!, pero ya tienes el maximo de 5 vidas.");
            } else System.out.println("No acertaste. El comerciante desaparece.");
        }

        // Cada ronda usa un objetivo secreto nuevo y una tirada aleatoria del monstruo.
        // Si las distancias empatan, ninguno avanza y se juega otra ronda.
        static boolean enfrentarEnemigo() {
            while (true) {
                int numeroSecreto = (int)(Math.random() * 10) + 1;
                int numeroEnemigo = (int)(Math.random() * 10) + 1;
                int numeroJugador = leerNumeroJugador();

                System.out.println("El monstruo eligio: " + numeroEnemigo);
                System.out.println("El numero secreto era: " + numeroSecreto);

                int distanciaJugador = Math.abs(numeroJugador - numeroSecreto);
                int distanciaEnemigo = Math.abs(numeroEnemigo - numeroSecreto);

                if (distanciaJugador < distanciaEnemigo) {
                    vidas++;
                    System.out.println("¡Ganaste! Avanzas y ganas una vida extra. Vidas: " + vidas);
                    return true;
                } else if (distanciaEnemigo < distanciaJugador) {
                    vidas--;
                    System.out.println("Gano el monstruo. Pierdes una vida y no avanzas.");
                    return false;
                } else {
                    System.out.println("Empate: nadie avanza. Se sortea otro numero y se repite el duelo.");
                }
            }
        }

        static int leerNumeroJugador() {
            while (true) {
                System.out.print("Elige un numero del 1 al 10: ");
                try {
                    int numero = Integer.parseInt(sc.nextLine().trim());
                    if (numero >= 1 && numero <= 10) return numero;
                    System.out.println("El numero debe estar entre 1 y 10.");
                } catch (NumberFormatException e) {
                    System.out.println("Solo se pueden introducir numeros.");
                }
            }
        }

        static int dificultadPremio() {
            return CANTIDAD_ENEMIGOS <= 8 ? 3 : CANTIDAD_ENEMIGOS <= 15 ? 5 : 8;
        }
        // Cuenta cuantas trampas quedan en el tablero
            static int  contarTrampasRestantes(){
            int contador = 0;
                for (int i = 0; i < FILAS; i++) {
                    for (int j = 0; j < COLUMNAS; j++) {
                        if (tablero[i][j] == TRAMPA) {
                            contador++;
                        }
                    }
                }
                return contador;
            }
            
            //Cuenta cuantos enemigos quedan en el tablero
            static int contarEnemigosRestantes(){
            int contador = 0;
                for (int i = 0; i < FILAS; i++) {
                    for (int j = 0; j < COLUMNAS; j++) {
                       if (tablero[i][j] == ENEMIGO) 
                           contador++;
                    }
                }
                return contador;
            }
}
