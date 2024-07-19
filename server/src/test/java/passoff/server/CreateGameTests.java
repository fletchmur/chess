package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import response.CreateGameResponse;
import service.AuthorizationService;
import service.CreateGameService;
import service.ErrorException;

public class CreateGameTests {
    private final CreateGameService createGameService = new CreateGameService();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    @BeforeEach
    void setUp() {
        authDAO.clear();
        gameDAO.clear();

        try {
            authDAO.createAuth(new AuthData("1234","fletcher"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void createNewGame() {
        CreateGameRequest request = new CreateGameRequest("1234","fletcher's game");
        try {
            CreateGameResponse response = createGameService.createGame(request);
            Assertions.assertEquals(new CreateGameResponse(1),response);
        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    public void createGameBadRequest() {
        CreateGameRequest request = new CreateGameRequest("1234",null);
        try {
            CreateGameResponse response = createGameService.createGame(request);
            Assertions.fail("created game from bad request");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(400,e.getErrorCode());
        }
    }
}
