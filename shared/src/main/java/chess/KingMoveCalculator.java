package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator implements ChessPieceMoveCalculator
{int[][] relativePositions = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};

    private boolean positionOnBoard(ChessBoard board, int row, int col)
    {
        int boardSize = board.getChessBoardSize();
        boolean row_in_range = row <= boardSize && row >= 1;
        boolean column_in_range = col <= boardSize && col >= 1;
        return row_in_range && column_in_range;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor)
    {

        Collection<ChessMove> moves = new HashSet<>();
        for(int[] relPos : relativePositions)
        {
            int checkRow = myPosition.getRow() + relPos[1];
            int checkCol = myPosition.getColumn() + relPos[0];

            if(!positionOnBoard(board, checkRow, checkCol))
            {
                continue;
            }

            ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPosition);

            if (checkPiece != null && checkPiece.getTeamColor() == myColor)
            {
                continue;
            }

            moves.add(new ChessMove(myPosition,checkPosition,null));
        }
        return moves;
    }
}
