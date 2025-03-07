package chess;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ChessBot {

    // Aumentamos la profundidad
    private static final int DEFAULT_DEPTH = 4;

    /**
     * Tabla de valores base por tipo de pieza (material).
     */
    private static final int PAWN_VALUE   = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE   = 500;
    private static final int QUEEN_VALUE  = 900;
    private static final int KING_VALUE   = 20000; 

    /**
     * Tablas para dar bonus/penalizaciones a cada pieza según su ubicación.
     * Estas están "orientadas" para BLANCAS. Para NEGRAS, se invertirá la fila.
     * Fuentes aproximadas: valores típicos usados en bots de ajedrez simplificados.
     */

    // Pawn positional table (para BLANCAS)
    private static final int[][] PAWN_TABLE = {
        {  0,   0,   0,   0,   0,   0,   0,   0},
        { 50,  50,  50,  50,  50,  50,  50,  50},
        { 10,  10,  20,  30,  30,  20,  10,  10},
        {  5,   5,  10,  25,  25,  10,   5,   5},
        {  0,   0,   0,  20,  20,   0,   0,   0},
        {  5,  -5, -10,   0,   0, -10,  -5,   5},
        {  5,  10,  10, -20, -20,  10,  10,   5},
        {  0,   0,   0,   0,   0,   0,   0,   0}
    };

    // Knight positional table
    private static final int[][] KNIGHT_TABLE = {
        {-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20,   0,   0,   0,   0, -20, -40},
        {-30,   0,  10,  15,  15,  10,   0, -30},
        {-30,   5,  15,  20,  20,  15,   5, -30},
        {-30,   0,  15,  20,  20,  15,   0, -30},
        {-30,   5,  10,  15,  15,  10,   5, -30},
        {-40, -20,   0,   5,   5,   0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    // Bishop positional table
    private static final int[][] BISHOP_TABLE = {
        {-20, -10, -10, -10, -10, -10, -10, -20},
        {-10,   0,   0,   0,   0,   0,   0, -10},
        {-10,   0,   5,  10,  10,   5,   0, -10},
        {-10,   5,   5,  10,  10,   5,   5, -10},
        {-10,   0,  10,  10,  10,  10,   0, -10},
        {-10,  10,  10,  10,  10,  10,  10, -10},
        {-10,   5,   0,   0,   0,   0,   5, -10},
        {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    // Rook positional table
    private static final int[][] ROOK_TABLE = {
        {  0,   0,   0,   0,   0,   0,   0,   0},
        {  5,  10,  10,  10,  10,  10,  10,   5},
        { -5,   0,   0,   0,   0,   0,   0,  -5},
        { -5,   0,   0,   0,   0,   0,   0,  -5},
        { -5,   0,   0,   0,   0,   0,   0,  -5},
        { -5,   0,   0,   0,   0,   0,   0,  -5},
        { -5,   0,   0,   0,   0,   0,   0,  -5},
        {  0,   0,   0,   5,   5,   0,   0,   0}
    };

    // Queen positional table
    private static final int[][] QUEEN_TABLE = {
        {-20, -10, -10,  -5,  -5, -10, -10, -20},
        {-10,   0,   0,   0,   0,   0,   0, -10},
        {-10,   0,   5,   5,   5,   5,   0, -10},
        { -5,   0,   5,   5,   5,   5,   0,  -5},
        {  0,   0,   5,   5,   5,   5,   0,  -5},
        {-10,   5,   5,   5,   5,   5,   0, -10},
        {-10,   0,   5,   0,   0,   0,   0, -10},
        {-20, -10, -10,  -5,  -5, -10, -10, -20}
    };

    // King positional table (midgame)
    private static final int[][] KING_TABLE = {
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-20, -30, -30, -40, -40, -30, -30, -20},
        {-10, -20, -20, -20, -20, -20, -20, -10},
        { 20,  20,   0,   0,   0,   0,  20,  20},
        { 20,  30,  10,   0,   0,  10,  30,  20}
    };

    // ============= Métodos principales =============

    /**
     * Encuentra el mejor movimiento con alpha-beta
     * profundizando hasta DEFAULT_DEPTH.
     */
    public Move findBestMove(ChessGame game, PieceColor botColor) {
        return findBestMove(game, botColor, DEFAULT_DEPTH);
    }

    /**
     * Encuentra el mejor movimiento con la profundidad indicada.
     */
    public Move findBestMove(ChessGame game, PieceColor botColor, int depth) {
        List<Move> moves = getAllMoves(game, botColor);
        if (moves.isEmpty()) {
            return null;
        }

        boolean isMaximizing = (botColor == PieceColor.WHITE);
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move bestMove = null;

        for (Move move : moves) {
            ChessPiece captured = game.getBoard().getPieceAt(move.toRow, move.toCol);

            // Realizar
            game.getBoard().movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);
            game.forceTurnChange();

            // alphaBeta
            int score = alphaBeta(game, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isMaximizing);

            // Deshacer
            game.getBoard().movePiece(move.toRow, move.toCol, move.fromRow, move.fromCol);
            game.getBoard().setPieceAt(move.toRow, move.toCol, captured);
            game.forceTurnChange();

            if (isMaximizing) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    /**
     * alpha-beta pruning
     */
    private int alphaBeta(ChessGame game, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || game.isGameOver()) {
            return evaluateBoard(game);
        }

        PieceColor color = isMaximizing ? PieceColor.WHITE : PieceColor.BLACK;
        List<Move> moves = getAllMoves(game, color);
        if (moves.isEmpty()) {
            return evaluateBoard(game);
        }

        if (isMaximizing) {
            int value = Integer.MIN_VALUE;
            for (Move move : moves) {
                ChessPiece captured = game.getBoard().getPieceAt(move.toRow, move.toCol);
                // Hacer
                game.getBoard().movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);
                game.forceTurnChange();

                int score = alphaBeta(game, depth - 1, alpha, beta, false);

                // Deshacer
                game.getBoard().movePiece(move.toRow, move.toCol, move.fromRow, move.fromCol);
                game.getBoard().setPieceAt(move.toRow, move.toCol, captured);
                game.forceTurnChange();

                value = Math.max(value, score);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // poda
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move move : moves) {
                ChessPiece captured = game.getBoard().getPieceAt(move.toRow, move.toCol);
                // Hacer
                game.getBoard().movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);
                game.forceTurnChange();

                int score = alphaBeta(game, depth - 1, alpha, beta, true);

                // Deshacer
                game.getBoard().movePiece(move.toRow, move.toCol, move.fromRow, move.fromCol);
                game.getBoard().setPieceAt(move.toRow, move.toCol, captured);
                game.forceTurnChange();

                value = Math.min(value, score);
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // poda
            }
            return value;
        }
    }

    /**
     * Genera todos los movimientos posibles para 'color'.
     */
    private List<Move> getAllMoves(ChessGame game, PieceColor color) {
        List<Move> all = new ArrayList<>();
        Board board = game.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == color) {
                    List<Point> validPoints = game.getValidMoves(row, col);
                    for (Point p : validPoints) {
                        all.add(new Move(row, col, p.x, p.y));
                    }
                }
            }
        }
        return all;
    }

    // ========== EVALUACIÓN (material + piece-square tables + movilidad) ==========

    private int evaluateBoard(ChessGame game) {
        int whiteScore = 0;
        int blackScore = 0;

        Board board = game.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    // Material base
                    int baseVal = getBaseValue(piece.getType());
                    // Bonus por posición
                    int positionalVal = getPositionalValue(piece.getType(), piece.getColor(), row, col);
                    // Suma total
                    int pieceVal = baseVal + positionalVal;

                    // Acumular para color
                    if (piece.getColor() == PieceColor.WHITE) {
                        whiteScore += pieceVal;
                    } else {
                        blackScore += pieceVal;
                    }
                }
            }
        }

        // Movilidad: cuántos moves tiene cada color
        int whiteMob = 0, blackMob = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    int moves = game.getValidMoves(row, col).size();
                    if (piece.getColor() == PieceColor.WHITE) {
                        whiteMob += moves;
                    } else {
                        blackMob += moves;
                    }
                }
            }
        }

        // Pequeño factor para movilidad
        double mobilityEval = 0.1 * (whiteMob - blackMob);

        int totalEval = (int)((whiteScore - blackScore) + mobilityEval);
        return totalEval;
    }

    /**
     * Retorna el valor "material base" de cada pieza.
     */
    private int getBaseValue(PieceType type) {
        switch (type) {
            case PAWN:   return PAWN_VALUE;
            case KNIGHT: return KNIGHT_VALUE;
            case BISHOP: return BISHOP_VALUE;
            case ROOK:   return ROOK_VALUE;
            case QUEEN:  return QUEEN_VALUE;
            case KING:   return KING_VALUE;
            default:     return 0;
        }
    }

    /**
     * Retorna un bonus/penalización según la casilla, basándose en
     * piece-square table. Para Negras, se invierte la fila.
     */
    private int getPositionalValue(PieceType type, PieceColor color, int row, int col) {
        int tableRow = row, tableCol = col;
        // Si es NEGRAS, espejamos la tabla
        // (fila 0 pasa a ser 7, fila 1 pasa a ser 6, etc.)
        if (color == PieceColor.BLACK) {
            tableRow = 7 - row;
        }

        switch (type) {
            case PAWN:
                return PAWN_TABLE[tableRow][tableCol];
            case KNIGHT:
                return KNIGHT_TABLE[tableRow][tableCol];
            case BISHOP:
                return BISHOP_TABLE[tableRow][tableCol];
            case ROOK:
                return ROOK_TABLE[tableRow][tableCol];
            case QUEEN:
                return QUEEN_TABLE[tableRow][tableCol];
            case KING:
                return KING_TABLE[tableRow][tableCol];
            default:
                return 0;
        }
    }
}