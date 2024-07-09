package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeam;
    private ChessBoard board;

    public ChessGame() {

        currentTeam = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get the moves you could potentially make based on the piece at start position
        //then filter them out if doing that move would leave the king in check
        ChessPiece currentPiece = board.getPiece(startPosition);
        TeamColor pieceColor = currentPiece.getTeamColor();
        Collection<ChessMove> potentialMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();

        for(ChessMove move : potentialMoves) {
            if(!leavesInCheck(move,pieceColor))
            {
                validMoves.add(move);
            }
        }

        return validMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    private boolean leavesInCheck(ChessMove move, TeamColor team)
    {
        //enact a move temporarily, see if the king is in check in this new state, then undo the move

        ChessPiece originalPieceAtEnd = board.getPiece(move.getEndPosition());
        ChessPiece pieceAtStart = board.getPiece(move.getStartPosition());

        board.addPiece(move.getEndPosition(), pieceAtStart);
        board.addPiece(move.getStartPosition(), null);

        boolean check = isInCheck(team);

        board.addPiece(move.getStartPosition(), pieceAtStart);
        board.addPiece(move.getEndPosition(), originalPieceAtEnd);

        return check;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    private ChessPosition getKingPosition(TeamColor color) {
        for(int i =0; i<board.getSize(); i++) {
            for(int j =0; j<board.getSize(); j++) {
                ChessPosition position = new ChessPosition(i+1, j+1);
                ChessPiece piece = board.getPiece(position);

                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color)
                {
                    return position;
                }
            }
        }
        return null;
    }

    private Collection<ChessMove> getTeamMoves(TeamColor color) {
        Collection<ChessMove> moves = new HashSet<>();
        for(int i =0; i<board.getSize(); i++) {
            for(int j =0; j<board.getSize(); j++) {
                ChessPosition position = new ChessPosition(i+1, j+1);
                ChessPiece piece = board.getPiece(position);

                if(piece != null && piece.getTeamColor() == color)
                {
                    moves.addAll(piece.pieceMoves(board,position));
                }
            }
        }

        return moves;
    }

    public boolean isInCheck(TeamColor teamColor) {

        //find the king's position
        //look through all responses my opponent could do
        //if any of the responses land on my king position then I am in check

        TeamColor opposingTeamColor = switch(teamColor){
            case WHITE -> TeamColor.BLACK;
            case BLACK -> TeamColor.WHITE;
        };

        ChessPosition kingPosition = getKingPosition(teamColor);
        Collection<ChessMove> moves = getTeamMoves(opposingTeamColor);
        HashSet<ChessPosition> endPositions = new HashSet<>();
        for(ChessMove move : moves) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions.contains(kingPosition);

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
