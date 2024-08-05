package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import request.JoinGameRequest;
import response.JoinGameResponse;
import model.GameData;
import exception.ErrorException;

public class JoinGameService {
    GameDAO gameDAO = new MySQLGameDAO();
    AuthDAO authDAO = new MySQLAuthDAO();

    private boolean canJoin(ChessGame.TeamColor playerColor,GameData game) throws ErrorException {
        boolean hasWhitePlayer = game.whiteUsername() != null;
        boolean hasBlackPlayer = game.blackUsername() != null;
        boolean canJoin = switch (playerColor) {
            case WHITE -> !hasWhitePlayer;
            case BLACK -> !hasBlackPlayer;
        };
        return canJoin;
    }

    public JoinGameResponse joinGame(JoinGameRequest request,String authToken) throws ErrorException {
        ChessGame.TeamColor playerColor = request.playerColor();
        Integer gameID = request.gameID();
        try {
            String username = authDAO.getAuth(authToken).username();
            assert username != null;

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
        catch (DataAccessException e) {
            throw new ErrorException(500, e.getMessage());
        }
    }
}
