package passoff.server;
import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
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
        try {
            LogoutResponse response = logoutService.logout("1234");
            Assertions.assertEquals(new LogoutResponse(),response);
        }
        catch (Exception e) {
            Assertions.fail("unexpected exception: " + e);
        }
    }

    @Test
    void nonExistentUser() {
        try {
            LogoutResponse response = logoutService.logout("45678");
            Assertions.fail("logged out an unauthorized user");
        }
        catch (ErrorException e) {
            Assertions.assertEquals(500,e.getErrorCode());
        }
    }
}
