package chess;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private ChessPiece[][] board;

    public Board() {
        board = new ChessPiece[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
        // Peones
        for (int col = 0; col < 8; col++) {
            board[1][col] = new ChessPiece(PieceType.PAWN, PieceColor.BLACK);
            board[6][col] = new ChessPiece(PieceType.PAWN, PieceColor.WHITE);
        }

        // Piezas mayores
        PieceType[] majorPieces = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
            PieceType.QUEEN, PieceType.KING, PieceType.BISHOP,
            PieceType.KNIGHT, PieceType.ROOK
        };

        for (int col = 0; col < 8; col++) {
            board[0][col] = new ChessPiece(majorPieces[col], PieceColor.BLACK);
            board[7][col] = new ChessPiece(majorPieces[col], PieceColor.WHITE);
        }
    }

    public ChessPiece getPieceAt(int row, int col) {
        return board[row][col];
    }

    public void setPieceAt(int row, int col, ChessPiece piece) {
        board[row][col] = piece;
    }

    /**
     * Mueve pieza en el tablero sin validar (se asume verificado).
     */
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        ChessPiece piece = getPieceAt(fromRow, fromCol);
        if (piece != null) {
            setPieceAt(toRow, toCol, piece);
            setPieceAt(fromRow, fromCol, null);
        }
    }

    /**
     * Devuelve los movimientos válidos de la pieza en (row,col),
     * incluyendo enroque (para el rey), capturas, etc.
     */
    public List<Point> getValidMoves(int row, int col, ChessGame game) {
        List<Point> validMoves = new ArrayList<>();
        ChessPiece piece = getPieceAt(row, col);
        if (piece == null) return validMoves;

        switch (piece.getType()) {
            case PAWN:
                addPawnMoves(validMoves, piece, row, col);
                break;
            case ROOK:
                addRookMoves(validMoves, row, col);
                break;
            case KNIGHT:
                addKnightMoves(validMoves, row, col);
                break;
            case BISHOP:
                addBishopMoves(validMoves, row, col);
                break;
            case QUEEN:
                addRookMoves(validMoves, row, col);
                addBishopMoves(validMoves, row, col);
                break;
            case KING:
                addKingMoves(validMoves, row, col, game);
                break;
        }

        return validMoves;
    }

    // --- Movimientos del peón ---
    private void addPawnMoves(List<Point> vm, ChessPiece piece, int row, int col) {
        int direction = (piece.getColor() == PieceColor.WHITE) ? -1 : 1;
        int nextRow = row + direction;

        // Movimiento simple
        if (inBounds(nextRow, col) && getPieceAt(nextRow, col) == null) {
            vm.add(new Point(nextRow, col));
        }

        // Movimiento doble inicial
        int startRow = (piece.getColor() == PieceColor.WHITE) ? 6 : 1;
        if (row == startRow) {
            int twoSteps = row + 2 * direction;
            if (inBounds(twoSteps, col)
                && getPieceAt(nextRow, col) == null
                && getPieceAt(twoSteps, col) == null) {
                vm.add(new Point(twoSteps, col));
            }
        }

        // Captura diagonal
        for (int d = -1; d <= 1; d += 2) {
            int nextCol = col + d;
            if (inBounds(nextRow, nextCol)) {
                ChessPiece target = getPieceAt(nextRow, nextCol);
                if (target != null && target.getColor() != piece.getColor()) {
                    vm.add(new Point(nextRow, nextCol));
                }
            }
        }
    }

    // --- Movimientos de torre ---
    private void addRookMoves(List<Point> vm, int row, int col) {
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] dir : dirs) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r,c)) {
                ChessPiece target = getPieceAt(r,c);
                if (target == null) {
                    vm.add(new Point(r,c));
                } else {
                    if (target.getColor() != getPieceAt(row,col).getColor()) {
                        vm.add(new Point(r,c));
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
    }

    // --- Movimientos de caballo ---
    private void addKnightMoves(List<Point> vm, int row, int col) {
        int[][] deltas = {
            {-2,-1}, {-2,1}, {-1,-2}, {-1,2},
            {1,-2}, {1,2}, {2,-1}, {2,1}
        };
        for (int[] d : deltas) {
            int r = row + d[0];
            int c = col + d[1];
            if (inBounds(r,c)) {
                ChessPiece target = getPieceAt(r, c);
                if (target == null || target.getColor() != getPieceAt(row, col).getColor()) {
                    vm.add(new Point(r,c));
                }
            }
        }
    }

    // --- Movimientos de alfil ---
    private void addBishopMoves(List<Point> vm, int row, int col) {
        int[][] dirs = {{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] dir : dirs) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r,c)) {
                ChessPiece target = getPieceAt(r,c);
                if (target == null) {
                    vm.add(new Point(r,c));
                } else {
                    if (target.getColor() != getPieceAt(row,col).getColor()) {
                        vm.add(new Point(r,c));
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
    }

    // --- Movimientos de rey + enroques ---
    private void addKingMoves(List<Point> vm, int row, int col, ChessGame game) {
        int[][] deltas = {
            {-1,-1},{-1,0},{-1,1},
            {0,-1},       {0,1},
            {1,-1}, {1,0}, {1,1}
        };
        for (int[] d : deltas) {
            int r = row + d[0];
            int c = col + d[1];
            if (inBounds(r,c)) {
                ChessPiece target = getPieceAt(r,c);
                if (target == null || target.getColor() != getPieceAt(row,col).getColor()) {
                    vm.add(new Point(r,c));
                }
            }
        }

        // Enroque (corto/largo) si no se movió el rey ni la torre:
        ChessPiece king = getPieceAt(row, col);
        if (king == null || king.getType() != PieceType.KING) return;
        if (game.hasKingMoved(king.getColor())) return;

        // Enroque corto (col 6)
        if (!game.hasRookMoved(king.getColor(), true)) {
            if (getPieceAt(row,5) == null && getPieceAt(row,6) == null) {
                vm.add(new Point(row, 6));
            }
        }
        // Enroque largo (col 2)
        if (!game.hasRookMoved(king.getColor(), false)) {
            if (getPieceAt(row,1) == null && getPieceAt(row,2) == null && getPieceAt(row,3) == null) {
                vm.add(new Point(row, 2));
            }
        }
    }

    private boolean inBounds(int r, int c) {
        return (r >= 0 && r < 8 && c >= 0 && c < 8);
    }
}