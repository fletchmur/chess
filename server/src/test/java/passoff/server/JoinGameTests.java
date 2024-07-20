package passoff.server;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import request.JoinGameRequest;
import response.JoinGameResponse;
import service.ErrorException;
import service.JoinGameService;

public class JoinGameTests {
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    JoinGameService joinGameService = new JoinGameService();


    void setUp(GameData game) {
        gameDAO.clear();
        authDAO.clear();
        try {
            authDAO.createAuth(new AuthData("123","fletcher"));
            gameDAO.createGame(game);
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void joinWhite() {
        setUp(new GameData(123,null,null,"test",new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE,123);
        try {
            JoinGameResponse response = joinGameService.joinGame(request,"123");
            Assertions.assertEquals(new JoinGameResponse(), response);
        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void joinBlack() {
        setUp(new GameData(123,null,null,"test",new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK,123);
        try {
            JoinGameResponse response = joinGameService.joinGame(request,"123");
            Assertions.assertEquals(new JoinGameResponse(), response);
        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void badJoinWhite() {
        setUp(new GameData(123,"emily",null,"test",new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE,123);
        try {
            JoinGameResponse response = joinGameService.joinGame(request,"123");
            Assertions.fail("joined game that already had white player");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(403, e.getErrorCode());
        }
    }

    @Test
    public void badJoinBlack() {
        setUp(new GameData(123,"emily","emily","test",new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK,123);
        try {
            JoinGameResponse response = joinGameService.joinGame(request,"123");
            Assertions.fail("joined game that already had white player");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(403, e.getErrorCode());
        }
    }
}
