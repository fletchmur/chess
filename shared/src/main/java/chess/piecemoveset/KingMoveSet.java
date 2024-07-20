package chess.piecemoveset;

import chess.*;

import java.util.Collection;

public class KingMoveSet extends PatternMoveCalculator implements MoveSet {
    private static final int[][] KING_PATTERN = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{-1,1},{-1,-1},{1,-1}};
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        return moveInPattern(KING_PATTERN, board, startingPosition, myColor);
    }
}
