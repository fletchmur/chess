package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import request.CreateGameRequest;
import response.CreateGameResponse;

import java.util.UUID;

public class CreateGameService {

    private AuthorizationService authService = new AuthorizationService();
    private GameDAO gameDAO = new MemoryGameDAO();

    public CreateGameResponse createGame(CreateGameRequest creationRequest) throws ErrorException {
        String authToken = creationRequest.authToken();
        String gameName = creationRequest.gameName();

        if(authToken == null || gameName == null) {
            throw new ErrorException(400, "bad request");
        }

        authService.authorize(authToken);

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
