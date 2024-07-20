package chess.piecemoveset;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveSet extends LineMoveCalculator implements MoveSet {

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        Collection<ChessMove> moves = new HashSet<>();

        Collection<ChessMove> upMoves = moveInLine(Direction.UP,board, startingPosition, myColor);
        Collection<ChessMove> downMoves = moveInLine(Direction.DOWN,board, startingPosition, myColor);
        Collection<ChessMove> leftMoves = moveInLine(Direction.LEFT,board, startingPosition, myColor);
        Collection<ChessMove> rightMoves = moveInLine(Direction.RIGHT,board, startingPosition, myColor);

        moves.addAll(upMoves);
        moves.addAll(downMoves);
        moves.addAll(leftMoves);
        moves.addAll(rightMoves);
        return moves;
    }
}
