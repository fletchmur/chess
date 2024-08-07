package client;

import chess.ChessBoard;
import chess.ChessGame;

public class ClientData {

    private ChessGame game;
    private String user;
    private ChessGame.TeamColor teamColor;
    private String authToken = "";
    private Integer gameID;

    public ClientData(ChessGame chessGame, String user) {
        this.game = chessGame;
        this.user = user;
        this.teamColor = ChessGame.TeamColor.WHITE;
    }

    public ChessGame getGame() {
        return game;
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
    public Integer getGameID() {
        return gameID;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public void updateChessBoard(ChessBoard board) {
        this.game.setBoard(board);
    }
    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
}
