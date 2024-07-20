package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import response.LoginResponse;
import service.ErrorException;
import service.LoginService;

public class LoginServiceTests {

    private final LoginService loginService = new LoginService();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private String uuid;

    @BeforeEach
    public void setup() {
        userDAO.clear();
        authDAO.clear();

        try {
            userDAO.createUser(new UserData("fletcher", "1234", "fletch@email.com"));
        }
        catch (Exception e) {
            System.out.println("Testing: User fletcher already in database");
        }
    }

    @Test
    public void loginExistingUser() {
        try {
            LoginResponse response = loginService.login(new LoginRequest("fletcher","1234"));
            Assertions.assertEquals(response,new LoginResponse("fletcher",response.authToken()));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void loginNonExistingUser() {
        try {
            LoginResponse response = loginService.login(new LoginRequest("bob","abc"));
            Assertions.fail("logged in an nonexistent user");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(e.getErrorCode(),401);
        }
    }

    @Test
    public void loginWrongPassword() {
        try {
            LoginResponse response = loginService.login(new LoginRequest("fletcher","1534"));
            Assertions.fail("logged in with invalid password");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(e.getErrorCode(),401);
        }
    }
}
