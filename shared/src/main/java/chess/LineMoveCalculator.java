package chess;

import java.util.Collection;
import java.util.HashSet;

public abstract class LineMoveCalculator {

    protected enum Direction {UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT};

    public Collection<ChessMove> moveInLine(Direction dir, ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        int rowIncrement = switch(dir)
        {
            case UP,UPLEFT,UPRIGHT -> 1;
            case DOWN,DOWNLEFT,DOWNRIGHT -> -1;
            case LEFT,RIGHT -> 0;
        };

        int colIncrement = switch(dir)
        {
            case RIGHT,UPRIGHT,DOWNRIGHT -> 1;
            case LEFT,UPLEFT, DOWNLEFT -> -1;
            case UP, DOWN -> 0;
        };

        Collection<ChessMove> moves = new HashSet<>();

        int checkRow = startingPosition.getRow() + rowIncrement;
        int checkCol = startingPosition.getColumn() + colIncrement;
        ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);

        while(checkPosition.isOnBoard(board))
        {
            ChessPiece checkPiece = board.getPiece(checkPosition);

            //if I encounter a piece that is nonempty
            if(checkPiece != null)
            {
                //see if I can capture it, then stop
                if(checkPiece.getTeamColor() != myColor)
                {
                    moves.add(new ChessMove(startingPosition,checkPosition,null));
                }
                break;
            }

            moves.add(new ChessMove(startingPosition,checkPosition,null));
            checkRow += rowIncrement;
            checkCol += colIncrement;
            checkPosition = new ChessPosition(checkRow, checkCol);
        }
        return moves;
    }
}
