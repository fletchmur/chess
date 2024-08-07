package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import response.RegisterResponse;
import exception.ErrorException;
import server.service.RegisterService;

public class RegisterServiceTests {
    private final RegisterService registerService = new RegisterService();
    private final UserDAO userDAO = new MySQLUserDAO();

    @BeforeEach
    public void setUp() {

        try {
            userDAO.clear();
            userDAO.createUser(new UserData("fletcher","1234","fletch@email.com"));
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void registerNewUser() {
        RegisterRequest request = new RegisterRequest("bob","abcde","bob@bobmail.net");
        try {
            RegisterResponse response = registerService.register(request);
            RegisterResponse desired = new RegisterResponse("bob",response.authToken());
            Assertions.assertEquals(response,desired);
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void registerExistingUser() {
        RegisterRequest request = new RegisterRequest("fletcher","123","fletcher@buisness.org");
        try {
            RegisterResponse response = registerService.register(request);
            Assertions.fail("registered existing user");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(403,e.getErrorCode());
        }
    }

    @Test
    public void badRequest() {
        RegisterRequest request = new RegisterRequest("fletcher",null,"fletcher@buisness.org");
        try {
            RegisterResponse response = registerService.register(request);
            Assertions.fail("registered a bad request");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(400,e.getErrorCode());
        }
    }
}
