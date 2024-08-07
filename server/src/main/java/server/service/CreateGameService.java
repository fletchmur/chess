package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import request.CreateGameRequest;
import response.CreateGameResponse;
import exception.ErrorException;

public class CreateGameService {

    private GameDAO gameDAO = new MySQLGameDAO();

    public CreateGameResponse createGame(CreateGameRequest creationRequest) throws ErrorException {
        String gameName = creationRequest.gameName();

        if(gameName == null) {
            throw new ErrorException(400, "bad request");
        }

        try {
            GameData game = new GameData(null,null,null,gameName,new ChessGame());
            int id = gameDAO.createGame(game);
            return new CreateGameResponse(id);
        }
        catch (DataAccessException e) {
            throw new ErrorException(500, e.getMessage());
        }
    }
}
