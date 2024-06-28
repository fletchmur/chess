package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator implements ChessPieceMoveCalculator
{
    private int[][] relativePositionsWhite = {{0,2},{0,1},{1,1},{-1,1}};
    private int[][] relativePositionsBlack = {{0,-2},{0,-1},{1,-1},{-1,-1}};

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

        // pawns move different directions based on the color
        int[][] relativePositions = switch (myColor)
        {
            case WHITE -> relativePositionsWhite;
            case BLACK -> relativePositionsBlack;
        };

        //pawns can move two on their first movement


        for (int i = 0; i < relativePositions.length; i++)
        {
            int[] relPos = relativePositions[i];
            int currentRow = myPosition.getRow() + relPos[1];
            int currentColumn = myPosition.getColumn() + relPos[0];

            //check if move is on board
            if (!positionOnBoard(board,currentRow,currentColumn))
            {
                continue;
            }

            ChessPosition currentPosition = new ChessPosition(currentRow, currentColumn);
            ChessPiece pieceAtCurrent = board.getPiece(currentPosition);

            // check to see if there is not a piece blocking
            if ((i == 0 || i == 1) && pieceAtCurrent != null)
            {
                continue;
            }
            //check to see if there are even pieces on the diagonals
            else if ((i == 2 || i == 3) && pieceAtCurrent == null)
            {
                continue;
            }
            // check to see if the pieces on my diagonal are on my team
            else if (pieceAtCurrent != null && pieceAtCurrent.getTeamColor() == myColor)
            {
                continue;
            }


            // if we are in the 1st or 8th row then do promotions otherwise just add one move
            if (currentRow == 1 || currentRow == 8)
            {
                for (ChessPiece.PieceType promotionType : promotions)
                {
                    moves.add(new ChessMove(myPosition,currentPosition,promotionType));
                }
            }
            else
            {
                if (i == 0 && !board.getPiece(myPosition).getFirstMove())
                {
                    continue;
                }
                moves.add(new ChessMove(myPosition,currentPosition,null));
            }
        }

        return moves;
    }
}
