package chess;

import java.util.Collection;

public interface MoveSet {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor);
}
