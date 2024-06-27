package chess;
import java.util.Collection;

public interface ChessPieceMoveCalculator {
    /*
    All moves calculators must have this method to calculate the valid moves for a
    piece given a board and position
    */
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor);
}
