package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveSet extends LineMoveCalculator implements MoveSet {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        Collection<ChessMove> moves = new HashSet<>();

        Collection<ChessMove> upLeftMoves = moveInLine(Direction.UPLEFT,board, startingPosition, myColor);
        Collection<ChessMove> upRightMoves = moveInLine(Direction.UPRIGHT,board, startingPosition, myColor);
        Collection<ChessMove> downLeftMoves = moveInLine(Direction.DOWNLEFT,board, startingPosition, myColor);
        Collection<ChessMove> downRightMoves = moveInLine(Direction.DOWNRIGHT,board, startingPosition, myColor);

        moves.addAll(upLeftMoves);
        moves.addAll(upRightMoves);
        moves.addAll(downLeftMoves);
        moves.addAll(downRightMoves);
        return moves;
    }
}
