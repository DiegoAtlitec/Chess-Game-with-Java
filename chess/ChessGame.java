package chess;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChessGame{

    private Board board;
    private PieceColor currentTurn;

    // Variables para enroque
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookLeftMoved = false;
    private boolean whiteRookRightMoved = false;
    private boolean blackRookLeftMoved = false;
    private boolean blackRookRightMoved = false;

    public static final int BOARD_SIZE = 8;

    // Constructores

    // Constructor por defecto (nuevo juego)
    public ChessGame() {
        board = new Board();
        currentTurn = PieceColor.WHITE; // Comienzan las blancas
    }

    // Constructor para cargar una partida existente
    public ChessGame(Board loadedBoard, PieceColor loadedTurn,
                     boolean wKingMoved, boolean bKingMoved,
                     boolean wRookLeftMoved, boolean wRookRightMoved,
                     boolean bRookLeftMoved, boolean bRookRightMoved) {
        this.board = loadedBoard;
        this.currentTurn = loadedTurn;
        this.whiteKingMoved = wKingMoved;
        this.blackKingMoved = bKingMoved;
        this.whiteRookLeftMoved = wRookLeftMoved;
        this.whiteRookRightMoved = wRookRightMoved;
        this.blackRookLeftMoved = bRookLeftMoved;
        this.blackRookRightMoved = bRookRightMoved;
    }

    // Métodos Getter
    public Board getBoard() {
        return board;
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Cambia manualmente el turno (usado por la IA en simulaciones).
     */
    public void forceTurnChange() {
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    /**
     * Mueve la pieza desde (fromRow, fromCol) hasta (toRow, toCol).
     * Incluye enroque, promoción y registro en la terminal de la notación.
     */
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        ChessPiece piece = board.getPieceAt(fromRow, fromCol);
        ChessPiece targetPiece = board.getPieceAt(toRow, toCol);

        if (piece == null || piece.getColor() != currentTurn) {
            return false;
        }

        // No puede capturar a pieza propia
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            return false;
        }

        // Revisar enroque
        if (isCastlingMove(piece, fromRow, fromCol, toRow, toCol)) {
            performCastling(piece, fromRow, fromCol, toRow, toCol);
            updateMovementFlags(piece, fromRow, fromCol, toRow, toCol);

            // Imprimir enroque en la terminal
            if (toCol == 6) {
                System.out.println("Movimiento: Enroque corto (" + piece.getColor() + ")");
            } else {
                System.out.println("Movimiento: Enroque largo (" + piece.getColor() + ")");
            }

            switchTurn();
            return true;
        }

        // Movimiento normal
        if (!isMoveValid(piece, fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        // Realizar el movimiento
        board.movePiece(fromRow, fromCol, toRow, toCol);
        updateMovementFlags(piece, fromRow, fromCol, toRow, toCol);

        // Promoción
        if (piece.getType() == PieceType.PAWN) {
            if ((piece.getColor() == PieceColor.WHITE && toRow == 0)
             || (piece.getColor() == PieceColor.BLACK && toRow == 7)) {
                PieceType chosenType = choosePromotionPiece(piece.getColor());
                board.setPieceAt(toRow, toCol, new ChessPiece(chosenType, piece.getColor()));

                System.out.println(piece.getColor() + " Pawn promociona a " + chosenType);
            }
        }

        // Imprimir en consola la notación del movimiento (ej. "WHITE PAWN: e2 -> e4")
        String fromNotation = getSquareNotation(fromRow, fromCol);
        String toNotation = getSquareNotation(toRow, toCol);
        System.out.println(piece.getColor() + " " + piece.getType() + ": " + fromNotation + " -> " + toNotation);

        switchTurn();
        return true;
    }

    /**
     * Convierte (row, col) en notación tipo "a1", "h8", etc.
     * Ejemplo: row=7, col=0 => "a1". row=0, col=7 => "h8".
     */
    public String getSquareNotation(int row, int col) {
        char file = (char) ('a' + col);   // 0->a, 1->b, ..., 7->h
        int rank = 8 - row;                // row=0->8, row=1->7, ..., row=7->1
        return "" + file + rank;
    }

    /**
     * Devuelve los movimientos válidos de la pieza en (fromRow, fromCol).
     */
    public List<Point> getValidMoves(int fromRow, int fromCol) {
        List<Point> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPieceAt(fromRow, fromCol);
        if (piece == null) return validMoves;
        if (piece.getColor() != currentTurn) return validMoves;

        validMoves = board.getValidMoves(fromRow, fromCol, this);

        // Evitar capturas propias, etc. (en Board ya se filtra, pero por seguridad)
        List<Point> filtered = new ArrayList<>();
        for (Point p : validMoves) {
            ChessPiece target = board.getPieceAt(p.x, p.y);
            if (target == null || target.getColor() != piece.getColor()) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    /**
     * Determina si la partida ha terminado: si falta el rey blanco o negro.
     */
    public boolean isGameOver() {
        boolean whiteKingPresent = false;
        boolean blackKingPresent = false;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ChessPiece pc = board.getPieceAt(row, col);
                if (pc != null && pc.getType() == PieceType.KING) {
                    if (pc.getColor() == PieceColor.WHITE) whiteKingPresent = true;
                    if (pc.getColor() == PieceColor.BLACK) blackKingPresent = true;
                }
            }
        }
        return (!whiteKingPresent || !blackKingPresent);
    }

    // ------------------- Validaciones de movimiento (sin enroque) ---------------------

    private boolean isMoveValid(ChessPiece piece, int fr, int fc, int tr, int tc) {
        switch (piece.getType()) {
            case PAWN:
                return isValidPawnMove(piece, fr, fc, tr, tc);
            case ROOK:
                return isValidRookMove(fr, fc, tr, tc);
            case KNIGHT:
                return isValidKnightMove(fr, fc, tr, tc);
            case BISHOP:
                return isValidBishopMove(fr, fc, tr, tc);
            case QUEEN:
                return isValidQueenMove(fr, fc, tr, tc);
            case KING:
                return isValidKingMove(fr, fc, tr, tc);
            default:
                return false;
        }
    }

    private boolean isValidPawnMove(ChessPiece piece, int fr, int fc, int tr, int tc) {
        int direction = (piece.getColor() == PieceColor.WHITE) ? -1 : 1;
        int startRow = (piece.getColor() == PieceColor.WHITE) ? 6 : 1;

        // Movimiento vertical
        if (fc == tc) {
            // Avance de 1
            if (tr == fr + direction && board.getPieceAt(tr, tc) == null) {
                return true;
            }
            // Avance de 2 (fila inicial)
            if (fr == startRow && tr == fr + 2 * direction) {
                if (board.getPieceAt(fr + direction, fc) == null
                 && board.getPieceAt(tr, tc) == null) {
                    return true;
                }
            }
            return false;
        }

        // Captura diagonal
        if (Math.abs(fc - tc) == 1 && tr == fr + direction) {
            ChessPiece target = board.getPieceAt(tr, tc);
            if (target != null && target.getColor() != piece.getColor()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidRookMove(int fr, int fc, int tr, int tc) {
        if (fr != tr && fc != tc) {
            return false;
        }
        // Revisar camino
        if (fr == tr) {
            int step = (tc > fc) ? 1 : -1;
            for (int c = fc + step; c != tc; c += step) {
                if (board.getPieceAt(fr, c) != null) {
                    return false;
                }
            }
        } else {
            int step = (tr > fr) ? 1 : -1;
            for (int r = fr + step; r != tr; r += step) {
                if (board.getPieceAt(r, fc) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidKnightMove(int fr, int fc, int tr, int tc) {
        int rowDiff = Math.abs(fr - tr);
        int colDiff = Math.abs(fc - tc);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    private boolean isValidBishopMove(int fr, int fc, int tr, int tc) {
        if (Math.abs(fr - tr) != Math.abs(fc - tc)) {
            return false;
        }
        int rowStep = (tr > fr) ? 1 : -1;
        int colStep = (tc > fc) ? 1 : -1;
        int r = fr + rowStep;
        int c = fc + colStep;
        while (r != tr && c != tc) {
            if (board.getPieceAt(r, c) != null) {
                return false;
            }
            r += rowStep;
            c += colStep;
        }
        return true;
    }

    private boolean isValidQueenMove(int fr, int fc, int tr, int tc) {
        return (isValidRookMove(fr, fc, tr, tc) || isValidBishopMove(fr, fc, tr, tc));
    }

    private boolean isValidKingMove(int fr, int fc, int tr, int tc) {
        int rowDiff = Math.abs(fr - tr);
        int colDiff = Math.abs(fc - tc);
        return (rowDiff <= 1 && colDiff <= 1);
    }

    // ------------------- Enroque ---------------------

    private boolean isCastlingMove(ChessPiece piece, int fr, int fc, int tr, int tc) {
        if (piece.getType() != PieceType.KING) return false;

        // Enroque corto (4->6)
        if (fc == 4 && tc == 6 && fr == tr) {
            if (!hasKingMoved(piece.getColor()) && !hasRookMoved(piece.getColor(), true)) {
                // Además, verificar que no haya piezas entre el rey y la torre
                if (board.getPieceAt(fr, 5) == null && board.getPieceAt(fr, 6) == null) {
                    return true;
                }
            }
        }
        // Enroque largo (4->2)
        if (fc == 4 && tc == 2 && fr == tr) {
            if (!hasKingMoved(piece.getColor()) && !hasRookMoved(piece.getColor(), false)) {
                // Además, verificar que no haya piezas entre el rey y la torre
                if (board.getPieceAt(fr, 1) == null && board.getPieceAt(fr, 2) == null && board.getPieceAt(fr, 3) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void performCastling(ChessPiece king, int fr, int fc, int tr, int tc) {
        board.movePiece(fr, fc, tr, tc);
        if (tc == 6) {
            // Enroque corto => mover torre (col=7 -> col=5)
            board.movePiece(fr, 7, fr, 5);
        } else if (tc == 2) {
            // Enroque largo => mover torre (col=0 -> col=3)
            board.movePiece(fr, 0, fr, 3);
        }
    }

    private void updateMovementFlags(ChessPiece piece, int fr, int fc, int tr, int tc) {
        PieceColor color = piece.getColor();
        if (piece.getType() == PieceType.KING) {
            if (color == PieceColor.WHITE) whiteKingMoved = true;
            else blackKingMoved = true;
        } else if (piece.getType() == PieceType.ROOK) {
            if (color == PieceColor.WHITE && fr == 7) {
                if (fc == 0) whiteRookLeftMoved = true;
                if (fc == 7) whiteRookRightMoved = true;
            }
            if (color == PieceColor.BLACK && fr == 0) {
                if (fc == 0) blackRookLeftMoved = true;
                if (fc == 7) blackRookRightMoved = true;
            }
        }
    }

    public boolean hasKingMoved(PieceColor color) {
        return (color == PieceColor.WHITE) ? whiteKingMoved : blackKingMoved;
    }

    public boolean hasRookMoved(PieceColor color, boolean isRight) {
        if (color == PieceColor.WHITE) {
            return isRight ? whiteRookRightMoved : whiteRookLeftMoved;
        } else {
            return isRight ? blackRookRightMoved : blackRookLeftMoved;
        }
    }

    private void switchTurn() {
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    /**
     * Muestra un diálogo para promocionar el peón.
     */
    private PieceType choosePromotionPiece(PieceColor color) {
        String[] options = {"Reina", "Torre", "Alfil", "Caballo"};
        int choice = javax.swing.JOptionPane.showOptionDialog(
                null,
                "Elige la pieza de promoción:",
                "Promoción de Peón",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice < 0) return PieceType.QUEEN;
        switch (choice) {
            case 0: return PieceType.QUEEN;
            case 1: return PieceType.ROOK;
            case 2: return PieceType.BISHOP;
            case 3: return PieceType.KNIGHT;
            default: return PieceType.QUEEN;
        }
    }

    // ============= MÉTODOS PARA GUARDAR Y CARGAR PARTIDA =============

    /**
     * Guarda el estado de la partida (incluyendo vsBot) en un archivo de texto.
     */
    public void saveGameToFile(String filePath, boolean vsBot) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            // 1) vsBot o PvP
            pw.println(vsBot ? "BOT" : "PVP");

            // 2) Turno actual
            pw.println(currentTurn.toString()); // "WHITE" o "BLACK"

            // 3) Flags de enroque
            pw.println(whiteKingMoved + " " + blackKingMoved + " "
                     + whiteRookLeftMoved + " " + whiteRookRightMoved + " "
                     + blackRookLeftMoved + " " + blackRookRightMoved);

            // 4) Posiciones de las piezas
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    ChessPiece piece = board.getPieceAt(row, col);
                    if (piece != null) {
                        // Formato: row col TYPE COLOR
                        pw.println(row + " " + col + " " + piece.getType() + " " + piece.getColor());
                    }
                }
            }

            pw.flush();
            System.out.println("Partida guardada en " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el estado de la partida desde un archivo de texto.
     * Devuelve true si la partida era vsBot, false si era PvP.
     */
    public boolean loadGameFromFile(String filePath) throws IOException {
        // 1) Restaurar un board vacío
        board = new Board();
        // Quitar las piezas iniciales (las que se pusieron en constructor)
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                board.setPieceAt(r, c, null);
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Leer si es BOT o PVP
            String vsLine = br.readLine();
            boolean loadedVsBot = vsLine.equals("BOT");

            // Leer el turno actual
            String turnLine = br.readLine();
            currentTurn = PieceColor.valueOf(turnLine); // "WHITE" o "BLACK"

            // Leer flags
            String flagsLine = br.readLine();
            String[] f = flagsLine.split(" ");
            whiteKingMoved      = Boolean.parseBoolean(f[0]);
            blackKingMoved      = Boolean.parseBoolean(f[1]);
            whiteRookLeftMoved  = Boolean.parseBoolean(f[2]);
            whiteRookRightMoved = Boolean.parseBoolean(f[3]);
            blackRookLeftMoved  = Boolean.parseBoolean(f[4]);
            blackRookRightMoved = Boolean.parseBoolean(f[5]);

            // Leer piezas
            String line;
            while ((line = br.readLine()) != null) {
                // row col TYPE COLOR
                String[] parts = line.split(" ");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                PieceType type = PieceType.valueOf(parts[2]);   // PAWN, ROOK, etc.
                PieceColor color = PieceColor.valueOf(parts[3]); // WHITE, BLACK

                board.setPieceAt(row, col, new ChessPiece(type, color));
            }

            System.out.println("Partida cargada desde " + filePath);
            return loadedVsBot;
        }
    }
}