package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator implements ChessPieceMoveCalculator
{
    private boolean positionOnBoard(ChessBoard board, int row, int col)
    {
        int boardSize = board.getChessBoardSize();
        boolean row_in_range = row <= boardSize && row >= 1;
        boolean column_in_range = col <= boardSize && col >= 1;
        return row_in_range && column_in_range;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor)
    {
        int currentRow = myPosition.getRow();
        int currentColumn = myPosition.getColumn();


        Collection<ChessMove> moves = new HashSet<>();
        //start at -1 to get all 9 spots
        for(int i = -1; i < 2; i++)
        {
            for (int j = -1; j < 2; j++)
            {
                // don't count center position
                if (i == 0 && j == 0)
                {
                    continue;
                }
                // don't let piece go off the board
                if (!positionOnBoard(board, currentRow + i, currentColumn + j))
                {
                    continue;
                }
                ChessPosition currentPosition = new ChessPosition(currentRow + i, currentColumn + j);
                ChessPiece pieceAtCurrent = board.getPiece(currentPosition);

                // can't move onto pieces of my same team
                if (pieceAtCurrent != null && pieceAtCurrent.getTeamColor() == myColor)
                {
                    continue;
                }
                moves.add(new ChessMove(myPosition,currentPosition,null));
            }

        }
        return moves;
    }
}
