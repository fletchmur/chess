package passoff.server;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import request.LogoutRequest;
import response.LogoutResponse;
import service.ErrorException;
import service.LogoutService;

public class LogoutTests {
    private final LogoutService logoutService = new LogoutService();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    @BeforeEach
    void setUp() {
        authDAO.clear();
        try {
            authDAO.createAuth(new AuthData("1234","fletcher"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void logoutUser() {
        LogoutRequest request = new LogoutRequest("1234");
        try {
            LogoutResponse response = logoutService.logout(request);
            Assertions.assertEquals(new LogoutResponse(),response);
        }
        catch (Exception e) {
            Assertions.fail("unexpected exception: " + e);
        }
    }

    @Test
    void nonExistentUser() {
        LogoutRequest request = new LogoutRequest("45678");
        try {
            LogoutResponse response = logoutService.logout(request);
            Assertions.fail("logged out an unauthorized user");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(401,e.getErrorCode());
        }
    }
}
