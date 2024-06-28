package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator implements ChessPieceMoveCalculator
{
    private final int[][] relativePositionsWhite = {{0,1},{1,1},{-1,1},{0,2}};
    private final int[][] relativePositionsBlack = {{0,-1},{1,-1},{-1,-1},{0,-2}};

    ChessPiece.PieceType[] promotions = {ChessPiece.PieceType.ROOK,ChessPiece.PieceType.KNIGHT,ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};

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
        int[][] relativePositions;
        int firstMoveRow;

        //change behavior depending on the color
        if (myColor == ChessGame.TeamColor.WHITE)
        {
            relativePositions = relativePositionsWhite;
            firstMoveRow = 2;
        }
        else
        {
            relativePositions = relativePositionsBlack;
            firstMoveRow = 7;
        }

        for (int i = 0; i < relativePositions.length; i++)
        {
            int[] relPos = relativePositions[i];
            int checkRow = myPosition.getRow() + relPos[1];
            int checkCol = myPosition.getColumn() + relPos[0];

            //check if move is on board
            if (!positionOnBoard(board,checkRow,checkCol))
            {
                continue;
            }

            ChessPosition checkPosition = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPosition);

            //FORWARD MOVEMENT CHECKS
            //if we are checking the double move but not in the correct move, don't add position
            if (i == 3 && myPosition.getRow() != firstMoveRow)
            {
                continue;
            }

            //check to see if there is a piece blocking our forward movement
            if (i == 0  && checkPiece != null)
            {
                continue;
            }

            // for the double move you must check two spaces
            if (i == 3)
            {
                ChessPosition secondCheckPosition = new ChessPosition(myPosition.getRow() + relativePositions[0][1], myPosition.getColumn());
                ChessPiece secondPieceAtCheck = board.getPiece(secondCheckPosition);

                if (secondPieceAtCheck != null || checkPiece != null)
                {
                    continue;
                }
            }

            //DIAGONAL MOVEMENT CHECKS

            //check to see if there are even pieces on the diagonals
            if ((i == 1 || i == 2) && checkPiece == null)
            {
                continue;
            }

            // check to see if the pieces on my diagonal are on my team
            if (checkPiece != null && checkPiece.getTeamColor() == myColor)
            {
                continue;
            }

            // if we are in the 1st or 8th row then do promotions otherwise just add one move
            if (checkRow == 1 || checkRow == 8)
            {
                for (ChessPiece.PieceType promotionType : promotions)
                {
                    moves.add(new ChessMove(myPosition,checkPosition,promotionType));
                }
            }
            else
            {
                moves.add(new ChessMove(myPosition,checkPosition,null));
            }
        }

        return moves;
    }
}
