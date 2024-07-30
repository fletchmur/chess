package client;

import chess.ChessGame;
import exception.ErrorException;
import org.junit.jupiter.api.*;
import request.*;
import response.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + Integer.toString(port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    public void cleanUp() {
        try {
            facade.clear(new ClearRequest());
        } catch (ErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    private String registerDefaultUser() throws ErrorException {
        RegisterRequest request = new RegisterRequest("fletchmur","123","fletch@email.com");
        RegisterResponse response = facade.register(request);
        return response.authToken();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clearTest() {
        try {
           ClearResponse response = facade.clear(new ClearRequest());
           Assertions.assertInstanceOf(ClearResponse.class,response);
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    public void createGameRequest() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest request = new CreateGameRequest("testGame");
            CreateGameResponse response = facade.createGame(request,authToken);
            Assertions.assertInstanceOf(CreateGameResponse.class,response);
            Assertions.assertNotNull(response.gameID());
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createGameBadRequest() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest request = new CreateGameRequest(null);
            facade.createGame(request,authToken);
        }
        catch (ErrorException e) {
            Assertions.assertEquals(400,e.getErrorCode());
        }
    }

    @Test
    public void createGameNotAuthorized() {
        try {
            String authToken = "abc";
            CreateGameRequest request = new CreateGameRequest("testGame");
            facade.createGame(request,authToken);
            Assertions.fail("created game when unauthorized");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(401,e.getErrorCode());
        }
    }

    @Test
    public void registerUser() {
        try {
            RegisterRequest request = new RegisterRequest("fletchmur","123","fletch@email.com");
            RegisterResponse response = facade.register(request);
            Assertions.assertInstanceOf(RegisterResponse.class,response);
            Assertions.assertEquals("fletchmur", response.username());
            Assertions.assertNotNull(response.authToken());
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void registerUserTwice() {
        try {
            RegisterRequest request = new RegisterRequest("fletchmur","123","fletch@email.com");
            facade.register(request);
            facade.register(request);
            Assertions.fail("registered user twice");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(403,e.getErrorCode());
        }
    }

    @Test
    void logout() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("fletchmur","123","fletch@email.com");
            String authToken = facade.register(registerRequest).authToken();
            LogoutResponse response = facade.logout(new LogoutRequest(),authToken);

            Assertions.assertInstanceOf(LogoutResponse.class,response);
        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void logoutUnauthorized() {
        try {
            facade.logout(new LogoutRequest(), "fake token");
            Assertions.fail("logout unauthorized");
        }
        catch(ErrorException e) {
            Assertions.assertEquals(401,e.getErrorCode());
        }
    }

    @Test
    void login() {
        try {
            String authToken = registerDefaultUser();
            facade.logout(new LogoutRequest(),authToken);

            LoginRequest request = new LoginRequest("fletchmur","123");
            LoginResponse response = facade.login(request);
            Assertions.assertInstanceOf(LoginResponse.class,response);
            Assertions.assertEquals("fletchmur", response.username());
            Assertions.assertNotNull(response.authToken());

        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void loginFakeUser() {
        try {
            LoginRequest request = new LoginRequest("fletchmur","123");
            LoginResponse response = facade.login(request);
            Assertions.fail("login fake user");

        }
        catch (ErrorException e) {
           Assertions.assertEquals(401,e.getErrorCode());
        }
    }

    @Test
    void getAllGames() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest request1 = new CreateGameRequest("testGame1");
            CreateGameRequest request2 = new CreateGameRequest("testGame2");
            facade.createGame(request1,authToken);
            facade.createGame(request2,authToken);

            ListGamesResponse response = facade.listGames(new ListGamesRequest(),authToken);
            Assertions.assertInstanceOf(ListGamesResponse.class,response);
            Assertions.assertEquals(2,response.games().length);

        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void getAllGamesUnauthorized() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest request1 = new CreateGameRequest("testGame1");
            CreateGameRequest request2 = new CreateGameRequest("testGame2");
            facade.createGame(request1,authToken);
            facade.createGame(request2,authToken);

            facade.logout(new LogoutRequest(),authToken);

            ListGamesResponse response = facade.listGames(new ListGamesRequest(),authToken);


        }
        catch (ErrorException e) {
            Assertions.assertEquals(401,e.getErrorCode());
        }
    }

    @Test
    void joinGameWhite() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
            CreateGameResponse createGameResponse = facade.createGame(createGameRequest,authToken);
            JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE,createGameResponse.gameID());
            JoinGameResponse response = facade.joinGame(request,authToken);
            Assertions.assertInstanceOf(JoinGameResponse.class,response);
        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void joinGameBlack() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
            CreateGameResponse createGameResponse = facade.createGame(createGameRequest,authToken);
            JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK,createGameResponse.gameID());
            JoinGameResponse response = facade.joinGame(request,authToken);
            Assertions.assertInstanceOf(JoinGameResponse.class,response);
        }
        catch (ErrorException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void joinGameWhiteTaken() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
            CreateGameResponse createGameResponse = facade.createGame(createGameRequest,authToken);

            JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE,createGameResponse.gameID());
             facade.joinGame(request,authToken);
             facade.joinGame(request,authToken);
             Assertions.fail("joined white team but it was already taken");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(403,e.getErrorCode());
        }
    }

    @Test
    void joinGameBlackTaken() {
        try {
            String authToken = registerDefaultUser();
            CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
            CreateGameResponse createGameResponse = facade.createGame(createGameRequest,authToken);

            JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK,createGameResponse.gameID());
            facade.joinGame(request,authToken);
            facade.joinGame(request,authToken);
            Assertions.fail("joined white team but it was already taken");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(403,e.getErrorCode());
        }
    }

}
