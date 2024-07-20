package service;
import chess.ChessGame;
import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import response.ListGamesResponse;

public class ListGameTests {

    ListGamesService listGameService = new ListGamesService();
    GameDAO gameDAO = new MemoryGameDAO();

    @BeforeEach
    void setUp() {
        gameDAO.clear();
    }

    @Test
    public void listNoGames() {
        ListGamesResponse response = listGameService.listGames();
        Assertions.assertEquals(0,response.games().length);
    }

    @Test
    public void listMultipleGames() {
        try {
            gameDAO.createGame(new GameData(1,"bob","fred","test1",new ChessGame()));
            gameDAO.createGame(new GameData(2,"bob","fred","test1",new ChessGame()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ListGamesResponse response = listGameService.listGames();
        Assertions.assertEquals(2,response.games().length);
    }

}
