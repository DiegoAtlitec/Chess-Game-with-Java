package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ChessReplayGUI extends JFrame {

    private ChessGame game;            
    private int currentMoveIndex = 0;  
    private String[] moves;            

    private Map<String, ImageIcon> pieceIcons;

    /**
     * Panel principal (BorderLayout) que contendrá:
     *  - topPanel (con botones)
     *  - centerPanel (tablero)
     */
    private JPanel mainPanel;
    private JPanel centerPanel;  // Aquí dibujamos el tablero

    public ChessReplayGUI() {
        setTitle("Reproducción de Partida");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new ChessGame();

        // TU LISTA COMPLETA DE MOVIMIENTOS ...
        moves = new String[] {
            "e2e4", "e7e5",    // 1. e4 e5
            "g1f3", "b8c6",    // 2. Cf3 Cc6
            "f1c4", "f8c5",    // 3. Ac4 Ac5
            "e1g1", "g8f6",    // 4. O-O Cf6
            "d2d3", "d7d6",    // 5. d3 d6
            "c1g5", "h7h6",    // 6. Ag5 h6
            "g5h4", "g7g5",    // 7. Ah4 g5
            "h4g3", "c8g4",    // 8. Ag3 Ag4
            "h2h3", "g4h5",    // 9. h3 Ah5
            "c4b5", "c5b6",    // 10. Ab5 Ab6
            "b1c3", "a7a6",    // 11. Cc3 a6
            "b5a4", "a6a5",    // 12. Aa4 a5
            "a4c6", "b7c6",    // 13. Axc6+ bxc6
            "d3d4", "e5d4",    // 14. d4 exd4
            "f3d4", "d8e7",    // 15. Cxd4 Dd7
            "d4c6", "e8d8",    // 16. Cxc6+ bxc6
            "c6d8",     // 17. Ad6+ Axd6++
        };

        pieceIcons = new HashMap<>();
        loadPieceIcons();

        // mainPanel con BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // ========== TOP PANEL CON BOTONES ==========
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        JButton backButton = new JButton("← Atrás");
        backButton.addActionListener(e -> goBackOneMove());
        topPanel.add(backButton);

        JButton forwardButton = new JButton("Adelante →");
        forwardButton.addActionListener(e -> advanceMove());
        topPanel.add(forwardButton);

        JButton exitButton = new JButton("Salir");
        exitButton.addActionListener(e -> {
            dispose();
            new ChessMainMenu();
        });
        topPanel.add(exitButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ========== CENTER PANEL CON EL TABLERO ==========
        centerPanel = new JPanel(); // Lo llenaremos con drawBoard()
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Dibuja el tablero inicialmente
        drawBoard();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void advanceMove() {
        if (currentMoveIndex >= moves.length) {
            JOptionPane.showMessageDialog(this, "La partida ya terminó.");
            return;
        }
        String move = moves[currentMoveIndex];
        boolean ok = applyMoveAlgebraic(move);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Movimiento inválido: " + move);
            return;
        }
        currentMoveIndex++;
        drawBoard(); // Redibuja sólo el tablero
    }

    private void goBackOneMove() {
        if (currentMoveIndex == 0) {
            JOptionPane.showMessageDialog(this, "No se puede retroceder más.");
            return;
        }
        currentMoveIndex--;
        rebuildPosition(currentMoveIndex);
        drawBoard(); // Redibuja sólo el tablero
    }

    private void rebuildPosition(int count) {
        game = new ChessGame();
        for (int i = 0; i < count; i++) {
            applyMoveAlgebraic(moves[i]);
        }
    }

    private boolean applyMoveAlgebraic(String move) {
        if (move.length() < 4) return false;
        String from = move.substring(0, 2);
        String to   = move.substring(2);

        int fromCol = from.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(from.charAt(1));
        int toCol   = to.charAt(0) - 'a';
        int toRow   = 8 - Character.getNumericValue(to.charAt(1));

        return game.movePiece(fromRow, fromCol, toRow, toCol);
    }

    /**
     * Dibuja el tablero en centerPanel, sin remover el topPanel.
     */
    private void drawBoard() {
        // Primero limpiamos centerPanel nada más
        centerPanel.removeAll();
        centerPanel.setLayout(new GridLayout(9, 9));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // Fila 0 => etiqueta vacía + ‘a’..’h’
        centerPanel.add(new JLabel(""));
        for (int col = 0; col < 8; col++) {
            char file = (char)('a' + col);
            JLabel lbl = new JLabel(String.valueOf(file), SwingConstants.CENTER);
            centerPanel.add(lbl);
        }

        // Filas 8..1 con las celdas
        for (int row = 0; row < 8; row++) {
            int rank = 8 - row;
            centerPanel.add(new JLabel(String.valueOf(rank), SwingConstants.CENTER));

            for (int col = 0; col < 8; col++) {
                centerPanel.add(createCellPanel(row, col));
            }
        }

        centerPanel.revalidate();
        centerPanel.repaint();
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
            } else {
                JLabel label = new JLabel(key, SwingConstants.CENTER);
                cellPanel.add(label, BorderLayout.CENTER);
            }
        }
        return cellPanel;
    }

    private void loadPieceIcons() {
        String basePath = "assets/images/";
        int cellSize = getWidth() / 8;

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