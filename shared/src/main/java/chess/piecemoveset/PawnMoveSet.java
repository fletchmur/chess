package chess.piecemoveset;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveSet implements MoveSet
{
    private static final ChessPiece.PieceType[] PROMOTION_TYPES =
            {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition startingPosition, ChessGame.TeamColor myColor)
    {
        Collection<ChessMove> moves = new HashSet<>();

        int direction;
        int doubleMoveRow;
        int promotionRow;

        if(myColor == ChessGame.TeamColor.WHITE)
        {
            direction = 1;
            doubleMoveRow = 2;
            promotionRow = board.getSize();
        }
        else
        {
            direction = -1;
            doubleMoveRow = board.getSize() -1;
            promotionRow = 1;
        }

        int[][] relativePositions = {{0,direction},{1,direction},{-1,direction},{0,direction*2}};

        for(int i = 0; i < relativePositions.length; i++)
        {
            int[] relativePosition = relativePositions[i];
            int checkRow = startingPosition.getRow() + relativePosition[1];
            int checkColumn = startingPosition.getColumn() + relativePosition[0];
            ChessPosition checkPosition = new ChessPosition(checkRow, checkColumn);

            if (!checkPosition.isOnBoard(board))
            {
                continue;
            }

            ChessPiece checkPiece = board.getPiece(checkPosition);

            //FORWARD MOVES
            if (i == 0 && checkPiece != null)
            {
                continue;
            }
            if (i == 3 && startingPosition.getRow() != doubleMoveRow)
            {
                continue;
            }

            if(i == 3 && startingPosition.getRow() == doubleMoveRow)
            {
                ChessPosition secondCheckPosition = new ChessPosition(startingPosition.getRow() + direction, startingPosition.getColumn());
                ChessPiece secondCheckPiece = board.getPiece(secondCheckPosition);

                if (checkPiece != null || secondCheckPiece != null)
                {
                    continue;
                }
            }

            //DIAGONAL MOVEMENT
            if (i == 1 || i == 2)
            {
                if (checkPiece == null || checkPiece.getTeamColor() == myColor)
                {
                    continue;
                }
            }

            //MOVE PIECE AND PROMOTION

            if (checkRow == promotionRow) {
                for (ChessPiece.PieceType pieceType : PROMOTION_TYPES)
                {
                    moves.add(new ChessMove(startingPosition,checkPosition,pieceType));
                }
            }
            else
            {
                moves.add(new ChessMove(startingPosition,checkPosition,null));
            }
        }

        return moves;
    }
}
