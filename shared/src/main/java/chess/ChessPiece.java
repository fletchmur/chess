package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //determine which moves calculator to create based on the pieceType field on the object and create a new instance of that object
        //TODO implement the move calculators for the other pieces
        ChessPieceMoveCalculator moveCalculator = switch (pieceType) {
            case KING -> throw new RuntimeException("Must implement King Move Calculator");
            case QUEEN -> throw new RuntimeException("Must implement Queen Move Calculator");
            case BISHOP -> new BishopMoveCalculator();
            case KNIGHT -> throw new RuntimeException("Must implement Knight Move Calculator");
            case ROOK -> throw new RuntimeException("Must implement Rook Move Calculator");
            case PAWN -> throw new RuntimeException("Must implement Pawn Move Calculator");
            default -> throw new RuntimeException("Must implement Null Move Calculator");
        };
        // return the valid moves based on the functionality of that move calculator
        return moveCalculator.calculateMoves(board, myPosition, pieceColor);
    }

    @Override
    public String toString() {
        return String.format("%s %s", pieceColor, pieceType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, getPieceType());
    }
}
