package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveCalculator implements ChessPieceMoveCalculator
{
    private boolean positionOnBoard(ChessBoard board, int row, int col)
    {
        int boardSize = board.getChessBoardSize();
        boolean row_in_range = row <= boardSize && row >= 1;
        boolean column_in_range = col <= boardSize && col >= 1;
        return row_in_range && column_in_range;
    }

    private enum Direction
    {
        UP, DOWN, LEFT, RIGHT;
    }

    private Collection<ChessMove> moveStraight(Direction dir, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myTeamColor)
    {
        Collection<ChessMove> moves = new HashSet<>();

        int colIncrement = switch (dir)
        {
            case UP,DOWN -> 0;
            case RIGHT -> -1;
            case LEFT -> 1;
        };

        int rowIncrement = switch (dir)
        {
            case UP -> 1;
            case DOWN -> -1;
            case LEFT,RIGHT -> 0;
        };

        int checkRow = myPosition.getRow() + rowIncrement;
        int checkCol = myPosition.getColumn() + colIncrement;

        while (positionOnBoard(board, checkRow, checkCol)) {
            ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPosition);
            // if its not empty
            if (checkPiece != null)
            {
                //if it's not on my team then capture the piece
                if (checkPiece.getTeamColor() != myTeamColor)
                {
                    moves.add(new ChessMove(myPosition,checkPosition,null));
                }
                //exit the loop when you encounter a non empty space
                break;
            }

            moves.add(new ChessMove(myPosition,checkPosition,null));
            checkRow += rowIncrement;
            checkCol += colIncrement;
        }
        return moves;

    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor)
    {
        Collection<ChessMove> allMoves = new HashSet<>();

        Collection<ChessMove> upMoves = moveStraight(Direction.UP, board, myPosition, myColor);
        Collection<ChessMove> downMoves = moveStraight(Direction.DOWN, board, myPosition, myColor);
        Collection<ChessMove> leftMoves = moveStraight(Direction.LEFT, board, myPosition, myColor);
        Collection<ChessMove> rightMoves = moveStraight(Direction.RIGHT, board, myPosition, myColor);

        allMoves.addAll(upMoves);
        allMoves.addAll(downMoves);
        allMoves.addAll(leftMoves);
        allMoves.addAll(rightMoves);

        return allMoves;
    }
}
