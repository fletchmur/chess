package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import model.GameData;
import request.CreateGameRequest;
import response.CreateGameResponse;

public class CreateGameService {

    private GameDAO gameDAO = new MemoryGameDAO();

    public CreateGameResponse createGame(CreateGameRequest creationRequest) throws ErrorException {
        String gameName = creationRequest.gameName();

        if(gameName == null) {
            throw new ErrorException(400, "bad request");
        }

        try {
            GameData game = new GameData(MemoryGameDAO.newGameID(),null,null,gameName,new ChessGame());
            gameDAO.createGame(game);
            return new CreateGameResponse(MemoryGameDAO.getCurrentID());
        }
        catch (DataAccessException e) {
            throw new ErrorException(500, e.getMessage());
        }
    }
}
