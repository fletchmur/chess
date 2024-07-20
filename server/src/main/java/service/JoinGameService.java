package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import request.JoinGameRequest;
import response.JoinGameResponse;
import model.GameData;

public class JoinGameService {
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    private boolean canJoin(ChessGame.TeamColor playerColor,GameData game) throws ErrorException {
        boolean hasWhitePlayer = game.whiteUsername() != null;
        boolean hasBlackPlayer = game.blackUsername() != null;
        boolean canJoin = switch (playerColor) {
            case WHITE -> !hasWhitePlayer;
            case BLACK -> !hasBlackPlayer;
        };
        return canJoin;
    }

    public JoinGameResponse joinGame(JoinGameRequest request) throws ErrorException {
        ChessGame.TeamColor playerColor = request.playerColor();
        Integer gameID = request.gameID();
        String username = authDAO.getAuth(request.authToken()).username();

        if (playerColor == null || gameID == null) {
            throw new ErrorException(400, "bad request");
        }

        GameData game = gameDAO.getGame(gameID);
        if (!canJoin(playerColor, game)) {
            throw new ErrorException(403, "already taken");
        }

        GameData newData = switch (playerColor) {
            case WHITE -> new GameData(gameID,username,game.blackUsername(),game.gameName(),game.game());
            case BLACK -> new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.game());
        };

        gameDAO.updateGame(gameID,newData);
        return new JoinGameResponse();
    }
}
