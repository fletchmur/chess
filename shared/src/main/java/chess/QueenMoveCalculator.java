package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveCalculator implements ChessPieceMoveCalculator
{
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor)
    {
        Collection<ChessMove> allMoves = new HashSet<>();

        BishopMoveCalculator diagonal = new BishopMoveCalculator();
        RookMoveCalculator straight = new RookMoveCalculator();

        Collection<ChessMove> diagonalMoves = diagonal.calculateMoves(board, myPosition, myColor);
        Collection<ChessMove> straightMoves = straight.calculateMoves(board, myPosition, myColor);

        allMoves.addAll(diagonalMoves);
        allMoves.addAll(straightMoves);

        return allMoves;

    }
}
