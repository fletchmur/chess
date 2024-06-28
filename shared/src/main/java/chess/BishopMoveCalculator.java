package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator implements ChessPieceMoveCalculator
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
        UPLEFT, DOWNLEFT, UPRIGHT, DOWNRIGHT;
    }

    private Collection<ChessMove> movesOnDiagonal(Direction dir, ChessBoard board, ChessPosition myPosition,ChessGame.TeamColor myColor) {
        Collection<ChessMove> moves = new HashSet<>();

        int colIncrement = switch (dir) {
            case UPRIGHT, DOWNRIGHT -> 1;
            case UPLEFT, DOWNLEFT -> -1;
        };

        int rowIncrement = switch (dir) {
            case UPRIGHT, UPLEFT -> 1;
            case DOWNRIGHT, DOWNLEFT -> -1;
        };

        //2 conditions for stop on board, fall off, or encounter piece to capture
        int checkRow = myPosition.getRow() + rowIncrement;
        int checkCol = myPosition.getColumn() + colIncrement;

        while (positionOnBoard(board, checkRow, checkCol)) {
            ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);
            ChessPiece pieceToCheck = board.getPiece(checkPosition);
            // if its not empty
            if (pieceToCheck != null)
            {
                //if it's not on my team then capture the piece
                if (pieceToCheck.getTeamColor() != myColor)
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

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myTeam)
    {
        //calculate all the valid moves for a bishop, the x pattern

        //RULES FOR THE BISHOP
        /*
        Bishops move in diagonal lines as far as there is open space.
        If there is an enemy piece at the end of the diagonal,
        the bishop may move to that position and capture the enemy piece.
         */
        Collection<ChessMove> upRight = movesOnDiagonal(Direction.UPRIGHT, board, myPosition, myTeam);
        Collection<ChessMove> upLeft = movesOnDiagonal(Direction.UPLEFT, board, myPosition, myTeam);
        Collection<ChessMove> downRight = movesOnDiagonal(Direction.DOWNRIGHT, board, myPosition, myTeam);
        Collection<ChessMove> downLeft = movesOnDiagonal(Direction.DOWNLEFT, board, myPosition, myTeam);

        Collection<ChessMove> allMoves = new HashSet<>();

        allMoves.addAll(upRight);
        allMoves.addAll(upLeft);
        allMoves.addAll(downRight);
        allMoves.addAll(downLeft);

        return allMoves;
    }

}
