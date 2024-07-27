package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.mysql.MySQLAuthDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import request.JoinGameRequest;
import response.JoinGameResponse;

public class JoinGameTests {
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MySQLAuthDAO();
    JoinGameService joinGameService = new JoinGameService();


    void setUp(GameData game) {

        try {
            gameDAO.clear();
            authDAO.clear();
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
