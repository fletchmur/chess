package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final int chessBoardSize = 8;
    private ChessPiece[][] board = new ChessPiece[chessBoardSize][chessBoardSize];

    public ChessBoard() {

    }

    public int getChessBoardSize()
    {
        return chessBoardSize;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int rowIndex = position.getRow()-1;
        int colIndex = position.getColumn()-1;
        return board[rowIndex][colIndex];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    private ChessPiece[] addBackRow(ChessGame.TeamColor teamColor)
    {
        ChessPiece[] row = new ChessPiece[chessBoardSize];
        row[0] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
        row[1] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        row[2] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        row[3] = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
        row[4] = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        row[5] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        row[6] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        row[7] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);

        return row;
    }

    private ChessPiece[] addPawnRow(ChessGame.TeamColor teamColor)
    {
        ChessPiece[] row = new ChessPiece[chessBoardSize];
        for (int i = 0; i < chessBoardSize; i++) {
            row[i] = new ChessPiece(teamColor,ChessPiece.PieceType.PAWN);
        }
        return row;
    }

    public void resetBoard() {

        board[0] = addBackRow(ChessGame.TeamColor.WHITE);
        board[1] = addPawnRow(ChessGame.TeamColor.WHITE);
        board[chessBoardSize-1] = addBackRow(ChessGame.TeamColor.BLACK);
        board[chessBoardSize-2] = addPawnRow(ChessGame.TeamColor.BLACK);

        for (int i = 2; i < chessBoardSize-2; i++)
        {
            for (int j = 0; j < chessBoardSize; j++)
            {
                board[i][j] = null;
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return getChessBoardSize() == that.getChessBoardSize() && Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChessBoardSize(), Arrays.deepHashCode(board));
    }
}
