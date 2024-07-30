package dataaccess;
import chess.ChessGame;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;

public class MySQLGameDAOTests {

    MySQLGameDAO mySQLGameDAO = new MySQLGameDAO();
    MySQLUserDAO mySQLUserDAO = new MySQLUserDAO();

    @BeforeEach
    void setUp() {
        try {
            mySQLGameDAO.clear();
            mySQLUserDAO.clear();

            mySQLUserDAO.createUser(new UserData("fletcher", "test", "test"));
            mySQLUserDAO.createUser(new UserData("emily", "test", "test"));
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createGame() {
        try {
            GameData gameData = new GameData(null,"fletcher","emily","testGame",new ChessGame());
            int id = mySQLGameDAO.createGame(gameData);
            Assertions.assertEquals(1,id);
            Assertions.assertEquals("fletcher",mySQLGameDAO.getGame(1).whiteUsername());
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    void createGameBad() {
        try {
            GameData gameData = new GameData(null,null,null,null,null);
            int id = mySQLGameDAO.createGame(gameData);
            Assertions.fail("Created game without name");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("trying to serialize null game", e.getMessage());
        }
    }

    @Test
    void getGame() {
        try {
            GameData gameData = new GameData(null,"fletcher","emily","testGame",new ChessGame());
            int id = mySQLGameDAO.createGame(gameData);
            Assertions.assertEquals(1,id);
            Assertions.assertEquals("fletcher",mySQLGameDAO.getGame(1).whiteUsername());
            Assertions.assertEquals("emily",mySQLGameDAO.getGame(1).blackUsername());
            Assertions.assertEquals("testGame",mySQLGameDAO.getGame(1).gameName());
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void getGameBad() {
        try {
            GameData game = mySQLGameDAO.getGame(1);
            Assertions.assertNull(game);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void getAllGames() {
        try {
            GameData game1 = new GameData(null,"fletcher","emily","testGame",new ChessGame());
            GameData game2 = new GameData(null,"sage","sam","testGame2",new ChessGame());
            mySQLGameDAO.createGame(game1);
            mySQLGameDAO.createGame(game2);

            Collection<GameData> games = mySQLGameDAO.getAllGames();
            Assertions.assertEquals(2, games.size());
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void getAllGamesBad() {
        try {
            Collection<GameData> games = mySQLGameDAO.getAllGames();
            Assertions.assertEquals(0, games.size());
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void updateGame() {
        try {
            GameData gameData = new GameData(null,"fletcher","emily","testGame",new ChessGame());
            int id = mySQLGameDAO.createGame(gameData);
            Assertions.assertEquals(1,id);
            Assertions.assertEquals("fletcher",mySQLGameDAO.getGame(1).whiteUsername());

            GameData newGameData = new GameData(null,"fletch","emily","testGame",new ChessGame());
            mySQLGameDAO.updateGame(1,newGameData);
            Assertions.assertEquals(1,id);
            Assertions.assertEquals("fletch",mySQLGameDAO.getGame(1).whiteUsername());
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void updateGameBad() {
        try {
            GameData gameData = new GameData(null,"fletcher","emily","testGame",new ChessGame());
            int id = mySQLGameDAO.createGame(gameData);
            Assertions.assertEquals(1,id);
            Assertions.assertEquals("fletcher",mySQLGameDAO.getGame(1).whiteUsername());

            GameData newGameData = new GameData(null,"fletch","emily","testGame",null);
            mySQLGameDAO.updateGame(1,newGameData);
            Assertions.fail("updated game with invalid null game");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("trying to serialize null game", e.getMessage());
        }
    }

    @Test
    void clearAllGames() {
        try {
            GameData game1 = new GameData(null,"fletcher","emily","testGame",new ChessGame());
            GameData game2 = new GameData(null,"sage","sam","testGame2",new ChessGame());
            mySQLGameDAO.createGame(game1);
            mySQLGameDAO.createGame(game2);

            Collection<GameData> games = mySQLGameDAO.getAllGames();
            Assertions.assertEquals(2, games.size());

            mySQLGameDAO.clear();
            games = mySQLGameDAO.getAllGames();
            Assertions.assertEquals(0, games.size());
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }


}
