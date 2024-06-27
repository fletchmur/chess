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

    private Collection<ChessMove> moveStraight(Direction dir, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myTeam)
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

        int currentRow = myPosition.getRow() + rowIncrement;
        int currentColumn = myPosition.getColumn() + colIncrement;

        while (positionOnBoard(board, currentRow, currentColumn)) {
            ChessPosition currentPosition = new ChessPosition(currentRow, currentColumn);
            ChessPiece pieceAtCurrent = board.getPiece(currentPosition);
            // if empty it is a valid move and add to the set
            if (pieceAtCurrent == null) {
                moves.add(new ChessMove(myPosition, currentPosition, null));
                currentRow += rowIncrement;
                currentColumn += colIncrement;
                continue;
            }
            //if it is a friendly piece then it is not a valid move, and we stop looking in this direction
            if (pieceAtCurrent.getTeamColor() == myTeam) {
                break;
            }
            //if it is an enemy piece then capture it
            moves.add(new ChessMove(myPosition, currentPosition, null));
            break;
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
