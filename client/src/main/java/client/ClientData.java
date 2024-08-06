package client;

import chess.ChessBoard;
import chess.ChessGame;

public class ClientData {

    private ChessGame board;
    private String user;
    private ChessGame.TeamColor teamColor;
    private String authToken = "";

    public ClientData(ChessGame chessGame, String user) {
        this.board = chessGame;
        this.user = user;
        this.teamColor = ChessGame.TeamColor.WHITE;
    }

    public ChessGame getBoard() {
        return board;
    }
    public String getUser() {
        return user;
    }
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
    public String getAuthToken() {
        return authToken;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public void updateChessBoard(ChessBoard board) {
        this.board.setBoard(board);
    }
    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
