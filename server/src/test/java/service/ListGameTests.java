package service;
import chess.ChessGame;
import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import response.ListGamesResponse;

public class ListGameTests {

    ListGamesService listGameService = new ListGamesService();
    GameDAO gameDAO = new MySQLGameDAO();

    @BeforeEach
    void setUp() {
        try {
            gameDAO.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listNoGames() {
        try {
            ListGamesResponse response = listGameService.listGames();
            Assertions.assertEquals(0,response.games().length);
        }
        catch (Exception e) {
            Assertions.fail("couldn't list no games");
        }
    }

    @Test
    public void listMultipleGames() {
        try {
            gameDAO.createGame(new GameData(1,"bob","fred","test1",new ChessGame()));
            gameDAO.createGame(new GameData(2,"fletch","emily","test2",new ChessGame()));
            ListGamesResponse response = listGameService.listGames();
            Assertions.assertEquals(2,response.games().length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    void tearDown() {
        try {
            gameDAO.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
