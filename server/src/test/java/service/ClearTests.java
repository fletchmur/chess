package service;
import chess.ChessGame;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

public class ClearTests {

    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();

    @Test
    public void testClear() {
        try {
            AuthData authorization = new AuthData("123", "fletcher");
            GameData game = new GameData(1, "fletcher", "emily", "test", new ChessGame());
            UserData user = new UserData("fletcher", "1234", "fletch@email.com");
            authDAO.createAuth(authorization);
            gameDAO.createGame(game);
            userDAO.createUser(user);

            authDAO.clear();
            gameDAO.clear();
            userDAO.clear();

            Assertions.assertNull(authDAO.getAuth("123"),"didn't clear authorization");
            Assertions.assertNull(gameDAO.getGame(1),"didn't clear game");
            Assertions.assertNull(userDAO.getUser("fletcher"),"didn't clear user");

        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
