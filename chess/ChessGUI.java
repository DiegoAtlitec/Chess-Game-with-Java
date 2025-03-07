package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Point;
import java.util.List;

public class ChessGUI extends JFrame {

    private static final int BOARD_SIZE = 8;
    private ChessGame game;
    private Map<String, ImageIcon> pieceIcons;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean pieceSelected = false;
    private List<Point> highlightedCells;
    private boolean vsBot = false;

    // Constructor normal (inicia un ChessGame nuevo)
    public ChessGUI() {
        initGUI(new ChessGame(), false);
    }

    // Constructor con "vsBot"
    public ChessGUI(boolean vsBot) {
        initGUI(new ChessGame(), vsBot);
    }

    // Constructor para cargar una partida ya existente
    public ChessGUI(ChessGame existingGame, boolean vsBot) {
        initGUI(existingGame, vsBot);
    }

    /**
     * Lógica común para inicializar la GUI.
     */
    private void initGUI(ChessGame gameInstance, boolean vsBotMode) {
        this.game = gameInstance;
        this.vsBot = vsBotMode;

        setTitle("Ajedrez - GUI");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pieceIcons = new HashMap<>();
        highlightedCells = new ArrayList<>();
        loadPieceIcons();

        setLayout(new BorderLayout());

        JPanel containerPanel = new JPanel(new BorderLayout());
        drawBoard(containerPanel);
        add(containerPanel, BorderLayout.CENTER);

        JButton pauseButton = new JButton("Pausa");
        pauseButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        pauseButton.setFocusPainted(false);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseButton.addActionListener(e -> showPauseMenu());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(pauseButton);
        topPanel.setBackground(containerPanel.getBackground());
        add(topPanel, BorderLayout.NORTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    showPauseMenu();
                }
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    /**
     * Menú de pausa con opciones: "Continuar", "Guardar", "Reiniciar", "Salir al menú".
     */
    private void showPauseMenu() {
        String[] options = {"Continuar", "Guardar", "Reiniciar", "Salir al menú"};
        int option = JOptionPane.showOptionDialog(
                this,
                "Juego en pausa",
                "Menú de Pausa",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
        switch (option) {
            case 0: // Continuar
                return;
            case 1: // Guardar
                // Guardar partida
                game.saveGameToFile("savegame.txt", vsBot);
                JOptionPane.showMessageDialog(this, "Partida guardada exitosamente.");
                break;
            case 2: // Reiniciar
                dispose();
                new ChessGUI(vsBot);
                break;
            case 3: // Salir al menú
                dispose();
                new ChessMainMenu();
                break;
        }
    }

    private void drawBoard(JPanel containerPanel) {
        containerPanel.removeAll();

        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Primera fila: vacía + a..h
        boardPanel.add(new JLabel(""));
        for (int col = 0; col < BOARD_SIZE; col++) {
            char file = (char)('a' + col);
            boardPanel.add(new JLabel(String.valueOf(file), SwingConstants.CENTER));
        }

        // Filas 8..1
        for (int row = 0; row < BOARD_SIZE; row++) {
            int rank = BOARD_SIZE - row;
            boardPanel.add(new JLabel(String.valueOf(rank), SwingConstants.CENTER));

            for (int col = 0; col < BOARD_SIZE; col++) {
                JPanel cellPanel = createCellPanel(row, col);
                boardPanel.add(cellPanel);
            }
        }

        containerPanel.add(boardPanel, BorderLayout.CENTER);
        containerPanel.revalidate();
        containerPanel.repaint();
    }

    private JPanel createCellPanel(int row, int col) {
        JPanel cellPanel = new JPanel(new BorderLayout());

        // Color del tablero
        if ((row + col) % 2 == 0) {
            cellPanel.setBackground(Color.WHITE);
        } else {
            cellPanel.setBackground(Color.GRAY);
        }

        // Resaltar si seleccionado
        if (pieceSelected && row == selectedRow && col == selectedCol) {
            cellPanel.setBackground(new Color(173, 216, 230));
        }

        // Resaltar movimientos posibles
        if (highlightedCells.contains(new Point(row, col))) {
            cellPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        }

        // Poner la pieza
        ChessPiece piece = game.getBoard().getPieceAt(row, col);
        if (piece != null) {
            String key = piece.getColor().toString() + piece.getType().toString();
            JLabel pieceLabel = new JLabel(pieceIcons.get(key));
            pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            pieceLabel.setVerticalAlignment(SwingConstants.CENTER);
            cellPanel.add(pieceLabel, BorderLayout.CENTER);
        }

        cellPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCellClick(row, col);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                ChessPiece pc = game.getBoard().getPieceAt(row, col);
                if (pc != null && pc.getColor() == game.getCurrentTurn()) {
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

    private void handleCellClick(int row, int col) {
        if (!pieceSelected) {
            // Seleccionar
            ChessPiece piece = game.getBoard().getPieceAt(row, col);
            if (piece != null && piece.getColor() == game.getCurrentTurn()) {
                selectedRow = row;
                selectedCol = col;
                pieceSelected = true;
                highlightedCells = game.getValidMoves(row, col);
                drawBoard((JPanel) getContentPane().getComponent(0));
            }
        } else {
            // Mover
            if (row == selectedRow && col == selectedCol) {
                pieceSelected = false;
                selectedRow = -1;
                selectedCol = -1;
                highlightedCells.clear();
                drawBoard((JPanel) getContentPane().getComponent(0));
                return;
            }

            if (game.movePiece(selectedRow, selectedCol, row, col)) {
                System.out.println("Movimiento realizado.");
                if (game.isGameOver()) {
                    String winner = (game.getCurrentTurn() == PieceColor.WHITE) ? "Negras" : "Blancas";
                    showGameOverDialog(winner);
                } else {
                    // Si vsBot y ahora es turno de las negras, mover Bot
                    if (vsBot && game.getCurrentTurn() == PieceColor.BLACK) {
                        makeBotMove();
                    }
                }
            } else {
                System.out.println("Movimiento inválido.");
            }

            pieceSelected = false;
            selectedRow = -1;
            selectedCol = -1;
            highlightedCells.clear();
            drawBoard((JPanel) getContentPane().getComponent(0));
        }
    }

    private void makeBotMove() {
        ChessBot bot = new ChessBot();
        Move bestMove = bot.findBestMove(game, PieceColor.BLACK);
        if (bestMove != null) {
            game.movePiece(bestMove.fromRow, bestMove.fromCol, bestMove.toRow, bestMove.toCol);
            drawBoard((JPanel) getContentPane().getComponent(0));

            if (game.isGameOver()) {
                String winner = (game.getCurrentTurn() == PieceColor.WHITE) ? "Negras" : "Blancas";
                showGameOverDialog(winner);
            }
        } else {
            System.out.println("El bot no tiene movimientos válidos (posible jaque mate o ahogado).");
        }
    }

    private void showGameOverDialog(String winner) {
        int option = JOptionPane.showOptionDialog(
                this,
                "¡" + winner + " han ganado!\n¿Qué deseas hacer?",
                "Fin del juego",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Reiniciar", "Salir al menú"},
                "Reiniciar"
        );
        if (option == 0) {
            dispose();
            new ChessGUI(vsBot);
        } else {
            dispose();
            new ChessMainMenu();
        }
    }

    private void loadPieceIcons() {
        String basePath = "assets/images/";
        int cellSize = getWidth() / BOARD_SIZE;

        pieceIcons.put("WHITEPAWN", loadScaledIcon(basePath + "wPawn.png", cellSize));
        pieceIcons.put("WHITEROOK", loadScaledIcon(basePath + "wRook.png", cellSize));
        pieceIcons.put("WHITEKNIGHT", loadScaledIcon(basePath + "wKnight.png", cellSize));
        pieceIcons.put("WHITEBISHOP", loadScaledIcon(basePath + "wBishop.png", cellSize));
        pieceIcons.put("WHITEQUEEN", loadScaledIcon(basePath + "wQueen.png", cellSize));
        pieceIcons.put("WHITEKING", loadScaledIcon(basePath + "wKing.png", cellSize));

        pieceIcons.put("BLACKPAWN", loadScaledIcon(basePath + "bPawn.png", cellSize));
        pieceIcons.put("BLACKROOK", loadScaledIcon(basePath + "bRook.png", cellSize));
        pieceIcons.put("BLACKKNIGHT", loadScaledIcon(basePath + "bKnight.png", cellSize));
        pieceIcons.put("BLACKBISHOP", loadScaledIcon(basePath + "bBishop.png", cellSize));
        pieceIcons.put("BLACKQUEEN", loadScaledIcon(basePath + "bQueen.png", cellSize));
        pieceIcons.put("BLACKKING", loadScaledIcon(basePath + "bKing.png", cellSize));
    }

    private ImageIcon loadScaledIcon(String path, int size) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}