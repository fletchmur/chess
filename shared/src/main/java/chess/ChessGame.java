package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final TurnHandler turnHandler;
    private ChessBoard board;

    public ChessGame() {

        turnHandler = new TurnHandler();
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return turnHandler.getCurrentTeam();
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */

    //THIS IS JUST FOR TESTING PURPOSES USE TAKE TURN INSTEAD
    public void setTeamTurn(TeamColor team) {
        turnHandler.setTeamTurn(team);
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
    //
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece movePiece = board.getPiece(startPosition);

        //check to see if the move is illegal
        if(movePiece == null) throw new InvalidMoveException();
        TeamColor pieceColor = movePiece.getTeamColor();
        if(!turnHandler.myTurn(pieceColor)) throw new InvalidMoveException();
        Collection<ChessMove> potentialMoves = validMoves(startPosition);
        if(!potentialMoves.contains(move)) throw new InvalidMoveException();

        ChessPosition endPosition = move.getEndPosition();

        //handle promotions
        if(move.getPromotionPiece() != null)
            movePiece = new ChessPiece(pieceColor, move.getPromotionPiece());


        board.addPiece(endPosition, movePiece);
        board.addPiece(startPosition, null);

        turnHandler.takeTurn();
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
        HashMap<ChessPosition, ChessPiece> pieces = board.getPiecesForTeam(color);

        for(HashMap.Entry<ChessPosition, ChessPiece> entry : pieces.entrySet())
        {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();

            if(piece.getPieceType() == ChessPiece.PieceType.KING)
            {
                return position;
            }
        }
        return null;
    }

    private Collection<ChessMove> getTeamPseudoMoves(TeamColor color) {
        Collection<ChessMove> moves = new HashSet<>();
        HashMap<ChessPosition,ChessPiece> pieces = board.getPiecesForTeam(color);
        for(HashMap.Entry<ChessPosition,ChessPiece> entry : pieces.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            moves.addAll(piece.pieceMoves(board,position));
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
        Collection<ChessMove> moves = getTeamPseudoMoves(opposingTeamColor);
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

        if(!isInCheck(teamColor)) return false;

        HashMap<ChessPosition, ChessPiece> pieces = board.getPiecesForTeam(teamColor);
        for(ChessPosition position : pieces.keySet()) {
            Collection<ChessMove> moves = validMoves(position);
            if(!moves.isEmpty()) return false;
        }
        //if we can't move then we are in checkmate
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheckmate(teamColor)) return false;
        HashMap<ChessPosition, ChessPiece> pieces = board.getPiecesForTeam(teamColor);
        for(ChessPosition position : pieces.keySet()) {
            Collection<ChessMove> moves = validMoves(position);
            if(!moves.isEmpty()) return false;
        }
        //if we can't move then we are in stalemate
        return true;


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
