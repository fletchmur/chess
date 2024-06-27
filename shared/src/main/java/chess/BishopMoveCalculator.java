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

    private Collection<ChessMove> movesOnDiagonal(Direction dir, ChessBoard board, ChessPosition myPosition,ChessGame.TeamColor myTeam) {
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
