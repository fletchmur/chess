package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.mysql.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import exception.ErrorException;
import server.service.AuthorizationService;

public class AuthorizationTests {
    private AuthorizationService authService = new AuthorizationService();
    private AuthDAO authDAO = new MySQLAuthDAO();

    @AfterEach
    public void tearDown() {
        try {
            authDAO.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @BeforeEach
    void setUp() {
        try {
            authDAO.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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
            Assertions.fail(e.getMessage());
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
