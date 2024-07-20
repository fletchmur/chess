package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

public class AuthorizationTests {
    private AuthorizationService authService = new AuthorizationService();
    private AuthDAO authDAO = new MemoryAuthDAO();

    @BeforeEach
    void setUp() {
        authDAO.clear();
    }

    @Test
    void authenticate()
    {
        try
        {
            authDAO.createAuth(new AuthData("1234","fletcher"));
            authService.authorize("1234");
        }
        catch (Exception e)
        {
            Assertions.fail("didn't authorize an authorized user");
        }
    }

    @Test
    void notAuthenticated() {
        try
        {
            authService.authorize("1234");
            Assertions.fail("Authorized fake authToken");
        }
        catch (ErrorException e)
        {
            Assertions.assertEquals(401,e.getErrorCode());
        }
    }
}
