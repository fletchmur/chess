package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private static final ChessPiece.PieceType[] backRow = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                                                            ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING,
                                                            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
    private static final ChessPiece.PieceType[] pawnRow = {ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN,
                                                            ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN, ChessPiece.PieceType.PAWN};

    private final int size = 8;
    private ChessPiece[][] board = new ChessPiece[size][size];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (!position.isOnBoard(this))
        {
            throw new IllegalArgumentException("cannot add to position not on board");
        }

        int row_index = position.getRow() - 1;
        int column_index = position.getColumn() - 1;

        board[row_index][column_index] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (!position.isOnBoard(this))
        {
            throw new IllegalArgumentException("cannot get piece from position not on board");
        }
        int row_index = position.getRow() - 1;
        int column_index = position.getColumn() - 1;
        return board[row_index][column_index];
    }

    public HashMap<ChessPosition, ChessPiece> getPiecesForTeam(ChessGame.TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> pieces = new HashMap<>();
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ChessPosition position = new ChessPosition(i+1,j+1);
                ChessPiece piece = getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor)
                    pieces.put(position, piece);
            }
        }
        return pieces;
    }

    public int getSize()
    {
        return this.size;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    private ChessPiece[] generateRow(ChessPiece.PieceType[] rowTemplate, ChessGame.TeamColor color)
    {
        if (rowTemplate.length != this.size)
        {
            throw new IllegalArgumentException("row template length does not match board size");
        }
        ChessPiece[] row = new ChessPiece[rowTemplate.length];

        for (int i = 0; i < rowTemplate.length; i++)
        {
            ChessPiece.PieceType pieceType = rowTemplate[i];
            row[i] = new ChessPiece(color, pieceType);
        }

        return row;
    }
    public void resetBoard() {
        ChessPiece[][] newBoard = new ChessPiece[size][size];

        newBoard[0] = generateRow(backRow, ChessGame.TeamColor.WHITE);
        newBoard[1] = generateRow(pawnRow, ChessGame.TeamColor.WHITE);
        newBoard[size-1] = generateRow(backRow, ChessGame.TeamColor.BLACK);
        newBoard[size-2] = generateRow(pawnRow, ChessGame.TeamColor.BLACK);

        board = newBoard;
    }
    @Override
    public String toString() {
        return "ChessBoard{" +
                "size=" + size +
                ", board=" + Arrays.toString(board) +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return getSize() == that.getSize() && Objects.deepEquals(board, that.board);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getSize(), Arrays.deepHashCode(board));
    }
}
