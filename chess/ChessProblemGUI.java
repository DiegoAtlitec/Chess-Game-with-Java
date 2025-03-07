package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Point;
import java.util.List;

public class ChessProblemGUI extends JFrame {

    private ChessGame game;         // Motor de ajedrez
    private boolean whitesTurn;     // Control de turno (solo blancas mueven)
    private int moveCount;          // Número de medio-movimientos
    private int moveLimit;          // Límite de medio-movimientos

    private JPanel mainPanel;
    private JPanel boardPanel;

    // Para selección de pieza
    private boolean pieceSelected = false;
    private int selectedRow = -1;
    private int selectedCol = -1;

    // Para “Reintentar”
    private int problemNumber;

    // Mapa de íconos de las piezas
    private Map<String, ImageIcon> pieceIcons;

    // Instancia de ChessBot
    private ChessBot chessBot;

    public ChessProblemGUI(int problemNumber) {
        this.problemNumber = problemNumber;

        setTitle("Problema " + problemNumber);
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1) Crear un ChessGame, vaciar su tablero
        game = new ChessGame();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                game.getBoard().setPieceAt(r, c, null);
            }
        }

        // 2) Definir la posición del problema y el límite
        setupProblem(problemNumber);

        // 3) Carga de íconos de piezas
        pieceIcons = new HashMap<>();
        loadPieceIcons(); // Método para cargar las imágenes

        // 4) Estructura de la GUI
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Panel superior con información y botón “Salir”
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel infoLabel = new JLabel(getProblemInfo(problemNumber), SwingConstants.CENTER);
        infoLabel.setFont(new Font("Serif", Font.BOLD, 16));
        topPanel.add(infoLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Salir");
        backButton.addActionListener(e -> {
            new ChessProblemMenu();
            dispose();
        });
        topPanel.add(backButton, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel central con el tablero
        boardPanel = new JPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Panel inferior con control de movimientos
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton resetButton = new JButton("Reiniciar Problema");
        resetButton.addActionListener(e -> {
            new ChessProblemGUI(problemNumber);
            dispose();
        });

        controlPanel.add(resetButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Inicializar ChessBot
        chessBot = new ChessBot();

        // Dibuja el tablero inicialmente
        drawBoard();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Proporciona información sobre el problema.
     */
    private String getProblemInfo(int problemNumber) {
        switch (problemNumber) {
            case 1:
                return "<html><center>Problema 1: Mate con 2 Torres<br>Blancas: 2 Torres y Rey<br>Negras: Solo Rey<br>Objetivo: Dar mate en menos de 10 movimientos.</center></html>";
            case 2:
                return "<html><center>Problema 2: Mate en 3 movimientos<br>Posición inicial:<br>Blancas: Rey en e1, Dama en h5, Caballo en f6<br>Negras: Rey en g8, Torre en e8<br>Solución: Dxh7+ Rf8, Dh8+ Re7, Cxe8#</center></html>";
            case 3:
                return "<html><center>Problema 3: Mate en 3 movimientos<br>Posición inicial:<br>Blancas: Rey en h1, Dama en h4, Alfil en c4<br>Negras: Rey en g7, Peones en f6 y h6<br>Solución: Dg4+ Rf8, Dh5 Rg7, Dh6#</center></html>";
            case 4:
                return "<html><center>Problema 4: Mate en 4 movimientos<br>Posición inicial:<br>Blancas: Rey en g1, Dama en e5, Torre en h1<br>Negras: Rey en g8, Torre en f8<br>Solución: Dh8+ Rf7, Th7+ Re8, Dg7 Tf7, Th8#</center></html>";
            case 5:
                return "<html><center>Problema 5: Mate en 4 movimientos<br>Posición inicial:<br>Blancas: Rey en e1, Dama en f6, Torre en d1<br>Negras: Rey en e8, Peones en f7 y e7<br>Solución: Dxe7+ Rxe7, Td7+ Re6, Df7+ Re5, Df5#</center></html>";
            // Añade más casos para problemas 6 a 10
            default:
                return "<html><center>Problema desconocido.</center></html>";
        }
    }

    /**
     * Configura la posición y el límite de jugadas de cada problema.
     */
    private void setupProblem(int problemNumber) {
        whitesTurn = true;
        moveCount = 0;

        switch (problemNumber) {
            case 1:
                // Problema 1: Blancas con 2 torres y rey vs Rey negro
                this.moveLimit = 20; // 10 movimientos (20 plies)
                // Blancas: Rey e1 => row=7,col=4
                game.getBoard().setPieceAt(7, 4, new ChessPiece(PieceType.KING, PieceColor.WHITE));
                // Torres a1 y h1 => row=7,col=0 y row=7,col=7
                game.getBoard().setPieceAt(7, 0, new ChessPiece(PieceType.ROOK, PieceColor.WHITE));
                game.getBoard().setPieceAt(7, 7, new ChessPiece(PieceType.ROOK, PieceColor.WHITE));
                // Negras: Rey e8 => row=0,col=4
                game.getBoard().setPieceAt(0, 4, new ChessPiece(PieceType.KING, PieceColor.BLACK));
                break;

            case 2:
                // Problema 2: Mate en 3 movimientos
                this.moveLimit = 6;
                // Blancas: Rey e1, Dama h5, Caballo f6
                game.getBoard().setPieceAt(7, 4, new ChessPiece(PieceType.KING, PieceColor.WHITE));
                game.getBoard().setPieceAt(3, 7, new ChessPiece(PieceType.QUEEN, PieceColor.WHITE));
                game.getBoard().setPieceAt(2, 5, new ChessPiece(PieceType.KNIGHT, PieceColor.WHITE));
                // Negras: Rey g8, Torre e8
                game.getBoard().setPieceAt(0, 6, new ChessPiece(PieceType.KING, PieceColor.BLACK));
                game.getBoard().setPieceAt(0, 4, new ChessPiece(PieceType.ROOK, PieceColor.BLACK));
                break;

            case 3:
                // Problema 3: Mate en 3 movimientos
                this.moveLimit = 6;
                // Blancas: Rey h1, Dama h4, Alfil c4
                game.getBoard().setPieceAt(7, 7, new ChessPiece(PieceType.KING, PieceColor.WHITE));
                game.getBoard().setPieceAt(4, 7, new ChessPiece(PieceType.QUEEN, PieceColor.WHITE));
                game.getBoard().setPieceAt(4, 2, new ChessPiece(PieceType.BISHOP, PieceColor.WHITE));
                // Negras: Rey g7, Peones f6 y h6
                game.getBoard().setPieceAt(1, 6, new ChessPiece(PieceType.KING, PieceColor.BLACK));
                game.getBoard().setPieceAt(2, 5, new ChessPiece(PieceType.PAWN, PieceColor.BLACK));
                game.getBoard().setPieceAt(2, 7, new ChessPiece(PieceType.PAWN, PieceColor.BLACK));
                break;

            case 4:
                // Problema 4: Mate en 4 movimientos
                this.moveLimit = 8;
                // Blancas: Rey g1, Dama e5, Torre h1
                game.getBoard().setPieceAt(7, 6, new ChessPiece(PieceType.KING, PieceColor.WHITE));
                game.getBoard().setPieceAt(3, 4, new ChessPiece(PieceType.QUEEN, PieceColor.WHITE));
                game.getBoard().setPieceAt(7, 7, new ChessPiece(PieceType.ROOK, PieceColor.WHITE));
                // Negras: Rey g8, Torre f8
                game.getBoard().setPieceAt(0, 6, new ChessPiece(PieceType.KING, PieceColor.BLACK));
                game.getBoard().setPieceAt(0, 5, new ChessPiece(PieceType.ROOK, PieceColor.BLACK));
                break;

            case 5:
                // Problema 5: Mate en 4 movimientos
                this.moveLimit = 8;
                // Blancas: Rey e1, Dama f6, Torre d1
                game.getBoard().setPieceAt(7, 4, new ChessPiece(PieceType.KING, PieceColor.WHITE));
                game.getBoard().setPieceAt(2, 5, new ChessPiece(PieceType.QUEEN, PieceColor.WHITE));
                game.getBoard().setPieceAt(7, 3, new ChessPiece(PieceType.ROOK, PieceColor.WHITE));
                // Negras: Rey e8, Peones f7 y e7
                game.getBoard().setPieceAt(0, 4, new ChessPiece(PieceType.KING, PieceColor.BLACK));
                game.getBoard().setPieceAt(1, 5, new ChessPiece(PieceType.PAWN, PieceColor.BLACK));
                game.getBoard().setPieceAt(1, 4, new ChessPiece(PieceType.PAWN, PieceColor.BLACK));
                break;

            // Puedes añadir más casos para problemas 6 a 10

            default:
                // Si no es 1..5, coloco algo genérico:
                this.moveLimit = 10;
                // Blancas: Rey e1
                game.getBoard().setPieceAt(7, 4, new ChessPiece(PieceType.KING, PieceColor.WHITE));
                // Negras: Rey e8
                game.getBoard().setPieceAt(0, 4, new ChessPiece(PieceType.KING, PieceColor.BLACK));
                break;
        }
    }

    // ====================== Método executeBlackMove ======================

    /**
     * Ejecuta el siguiente movimiento de las negras utilizando ChessBot.
     */
    private void executeBlackMove() {
        Move blackMove = chessBot.findBestMove(game, PieceColor.BLACK);
        if (blackMove != null) {
            System.out.println("ChessBot intenta mover de (" + blackMove.fromRow + "," + blackMove.fromCol + ") a (" + blackMove.toRow + "," + blackMove.toCol + ")");
            boolean moved = game.movePiece(blackMove.fromRow, blackMove.fromCol, blackMove.toRow, blackMove.toCol);
            if (moved) {
                moveCount++;
                System.out.println("ChessBot movió correctamente.");
                drawBoard();
                checkProblemStatus();
            } else {
                // Movimiento inválido, posible error en la lógica del bot
                System.out.println("ChessBot intentó un movimiento inválido.");
                JOptionPane.showMessageDialog(this, "ChessBot intentó un movimiento inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                showFailDialog();
            }
        } else {
            // No hay movimientos válidos, el juego puede haber terminado
            System.out.println("ChessBot no tiene movimientos válidos.");
            checkProblemStatus();
        }
    }

    /**
     * Verifica si se cumplió el mate o si se superó el límite de jugadas.
     */
    private void checkProblemStatus() {
        // 1) ¿Se logró mate?
        if (game.isGameOver()) {
            System.out.println("El juego ha terminado. Se ha logrado mate.");
            showWinDialog();
            return;
        }
        // 2) ¿Excedimos el límite?
        if (moveCount >= moveLimit) {
            System.out.println("Se ha excedido el límite de jugadas.");
            showFailDialog();
        }
    }

    private void showWinDialog() {
        // Dialogo de Felicitaciones, etc.
        int option = JOptionPane.showOptionDialog(
            this,
            "¡Has resuelto el problema!",
            "¡Felicidades!",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Reintentar", "Volver al Menú de Problemas", "Volver al Menú Principal"},
            "Reintentar"
        );
        switch (option) {
            case 0:
                new ChessProblemGUI(problemNumber);
                dispose();
                break;
            case 1:
                new ChessProblemMenu();
                dispose();
                break;
            case 2:
                new ChessMainMenu();
                dispose();
                break;
            default:
                // Por si se cierra el diálogo
                new ChessMainMenu();
                dispose();
                break;
        }
    }

    private void showFailDialog() {
        int option = JOptionPane.showOptionDialog(
            this,
            "No lograste resolverlo en menos de " + (moveLimit / 2) + " jugadas.",
            "Problema Fallido",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new String[]{"Reintentar", "Volver al Menú de Problemas", "Volver al Menú Principal"},
            "Reintentar"
        );
        switch (option) {
            case 0:
                new ChessProblemGUI(problemNumber);
                dispose();
                break;
            case 1:
                new ChessProblemMenu();
                dispose();
                break;
            case 2:
                new ChessMainMenu();
                dispose();
                break;
            default:
                // Por si se cierra el diálogo
                new ChessMainMenu();
                dispose();
                break;
        }
    }

    // ============= Lógica de dibujo del tablero con íconos =============

    /**
     * Carga los íconos en un mapa (tal como en ChessGUI).
     */
    private void loadPieceIcons() {
        // Ajusta la ruta según tu carpeta de imágenes
        String basePath = "assets/images/";
        int cellSize = 800 / 8; // Ajustar según el tamaño de la ventana

        pieceIcons.put("WHITEPAWN",   loadScaledIcon(basePath + "wPawn.png", cellSize));
        pieceIcons.put("WHITEROOK",   loadScaledIcon(basePath + "wRook.png", cellSize));
        pieceIcons.put("WHITEKNIGHT", loadScaledIcon(basePath + "wKnight.png", cellSize));
        pieceIcons.put("WHITEBISHOP", loadScaledIcon(basePath + "wBishop.png", cellSize));
        pieceIcons.put("WHITEQUEEN",  loadScaledIcon(basePath + "wQueen.png", cellSize));
        pieceIcons.put("WHITEKING",   loadScaledIcon(basePath + "wKing.png", cellSize));

        pieceIcons.put("BLACKPAWN",   loadScaledIcon(basePath + "bPawn.png", cellSize));
        pieceIcons.put("BLACKROOK",   loadScaledIcon(basePath + "bRook.png", cellSize));
        pieceIcons.put("BLACKKNIGHT", loadScaledIcon(basePath + "bKnight.png", cellSize));
        pieceIcons.put("BLACKBISHOP", loadScaledIcon(basePath + "bBishop.png", cellSize));
        pieceIcons.put("BLACKQUEEN",  loadScaledIcon(basePath + "bQueen.png", cellSize));
        pieceIcons.put("BLACKKING",   loadScaledIcon(basePath + "bKing.png", cellSize));
    }

    private ImageIcon loadScaledIcon(String path, int size) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Redibuja el tablero con un GridLayout(8,8).
     */
    private void drawBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(9, 9));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // Fila 0 => etiqueta vacía + ‘a’..’h’
        boardPanel.add(new JLabel(""));
        for (int col = 0; col < 8; col++) {
            char file = (char)('a' + col);
            boardPanel.add(new JLabel(String.valueOf(file), SwingConstants.CENTER));
        }

        // Filas 8..1 con las celdas
        for (int row = 0; row < 8; row++) {
            int rank = 8 - row;
            boardPanel.add(new JLabel(String.valueOf(rank), SwingConstants.CENTER));

            for (int col = 0; col < 8; col++) {
                boardPanel.add(createCellPanel(row, col));
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private JPanel createCellPanel(int row, int col) {
        JPanel cellPanel = new JPanel(new BorderLayout());
        if ((row + col) % 2 == 0) {
            cellPanel.setBackground(Color.WHITE);
        } else {
            cellPanel.setBackground(Color.GRAY);
        }

        ChessPiece piece = game.getBoard().getPieceAt(row, col);
        if (piece != null) {
            String key = piece.getColor().toString() + piece.getType().toString();
            ImageIcon icon = pieceIcons.get(key);
            if (icon != null) {
                JLabel label = new JLabel(icon);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                cellPanel.add(label, BorderLayout.CENTER);
            }
        }

        // Resaltar celdas si es un movimiento válido
        if (pieceSelected && highlightedCells.contains(new Point(row, col))) {
            cellPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        }

        final int r = row;
        final int c = col;
        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(r, c);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                ChessPiece pc = game.getBoard().getPieceAt(r, c);
                if (pc != null && pc.getColor() == PieceColor.WHITE) {
                    cellPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    cellPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cellPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return cellPanel;
    }

    // Variables para selección de pieza
    private List<Point> highlightedCells = new ArrayList<>();

    private void handleClick(int row, int col) {
        if (!pieceSelected) {
            // Intentar seleccionar
            ChessPiece piece = game.getBoard().getPieceAt(row, col);
            if (piece != null) {
                // Validar que sea de color blanco
                if (piece.getColor() == PieceColor.WHITE) {
                    selectedRow = row;
                    selectedCol = col;
                    pieceSelected = true;
                    highlightedCells = game.getValidMoves(row, col);
                    System.out.println("Seleccionada pieza blanca en (" + row + "," + col + ")");
                }
            }
        } else {
            // Intentar mover
            if (game.movePiece(selectedRow, selectedCol, row, col)) {
                moveCount++;
                System.out.println("Movimiento blanco de (" + selectedRow + "," + selectedCol + ") a (" + row + "," + col + ")");
                drawBoard();
                // Ejecutar el siguiente movimiento de las negras usando ChessBot
                executeBlackMove();
            } else {
                System.out.println("Movimiento inválido de blanco de (" + selectedRow + "," + selectedCol + ") a (" + row + "," + col + ")");
            }
            // Deseleccionar en cualquier caso
            pieceSelected = false;
            selectedRow = -1;
            selectedCol = -1;
            highlightedCells.clear();
        }
        drawBoard();
    }

    

}

