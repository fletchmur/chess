package chess;

import java.util.Collection;

public class KnightMoveSet extends PatternMoveCalculator implements MoveSet {
    private static final int[][] knightPattern = {{1,2},{2,1},{-1,2},{-2,1},{-1,-2},{-2,-1},{1,-2},{2,-1}};
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        return moveInPattern(knightPattern,board,startingPosition,myColor);
    }
}
