package chess.piecemoveset;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveSet implements MoveSet {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        Collection<ChessMove> moves = new HashSet<>();

        RookMoveSet rookMoveSet = new RookMoveSet();
        BishopMoveSet bishopMoveSet = new BishopMoveSet();

        moves.addAll(rookMoveSet.calculateMoves(board, startingPosition, myColor));
        moves.addAll(bishopMoveSet.calculateMoves(board,startingPosition,myColor));

        return moves;
    }
}
