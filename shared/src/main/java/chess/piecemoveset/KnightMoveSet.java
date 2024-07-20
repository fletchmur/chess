package chess.piecemoveset;

import chess.*;

import java.util.Collection;

public class KnightMoveSet extends PatternMoveCalculator implements MoveSet {
    private static final int[][] KNIGHT_PATTERN = {{1,2},{2,1},{-1,2},{-2,1},{-1,-2},{-2,-1},{1,-2},{2,-1}};
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        return moveInPattern(KNIGHT_PATTERN,board,startingPosition,myColor);
    }
}
