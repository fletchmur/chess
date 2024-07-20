package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import response.RegisterResponse;

public class RegisterServiceTests {
    private final RegisterService registerService = new RegisterService();
    private final UserDAO userDAO = new MemoryUserDAO();

    @BeforeEach
    public void setUp() {
        userDAO.clear();

        try {
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
