package chess;

import java.util.Collection;

public class KingMoveSet extends PatternMoveCalculator implements MoveSet {
    private static final int[][] kingPattern = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{-1,1},{-1,-1},{1,-1}};
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        return moveInPattern(kingPattern, board, startingPosition, myColor);
    }
}
