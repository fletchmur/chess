package chess;

import java.util.Collection;
import java.util.HashSet;

public abstract class PatternMoveCalculator {

    // patterns are 2d arrays of integers {{x,y}} that define the relative positions away from the piece's space to consider for valid moves
    // x = cols, y = rows

    public Collection<ChessMove> moveInPattern(int[][] pattern, ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor) {

        Collection<ChessMove> moves = new HashSet<>();

        for(int[] relativePosition : pattern)
        {
            int checkRow = startingPosition.getRow() + relativePosition[1];
            int checkCol = startingPosition.getColumn() + relativePosition[0];
            ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);

            //don't move off the board
            if (!checkPosition.isOnBoard(board))
            {
                continue;
            }

            ChessPiece checkPiece = board.getPiece(checkPosition);

            // don't move onto a space occupied by one on your team
            if (checkPiece != null && checkPiece.getTeamColor() == myColor)
            {
                continue;
            }

            moves.add(new ChessMove(startingPosition,checkPosition,null));
        }
        return moves;
    }
}
