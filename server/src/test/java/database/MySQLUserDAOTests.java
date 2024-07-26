package database;
import dataaccess.DataAccessException;
import dataaccess.mysql.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

public class MySQLUserDAOTests {

    MySQLUserDAO userDAO = new MySQLUserDAO();

    public MySQLUserDAOTests() throws DataAccessException {
    }

    @BeforeEach
    void setUp() {
        try {
            userDAO.clear();
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUser() {
        UserData user = new UserData("first","123password","first@first.com");
        try {
            String result = userDAO.createUser(user);
            Assertions.assertEquals(result, "first");
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void reregisterUser() {
        UserData user = new UserData("first","123password","first@first.com");
        try {
            userDAO.createUser(user);
            userDAO.createUser(user);
            Assertions.fail("reregistering user should fail");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals(e.getMessage(), "username taken");
        }

    }

    @Test
    public void getUser() {
        UserData user = new UserData("first","123password","first@first.com");
        try {
            userDAO.createUser(user);
            UserData userResult = userDAO.getUser("first");
            Assertions.assertEquals(userResult, user);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void notRegisteredUser() {
        try {
            Assertions.assertNull(userDAO.getUser("first"));
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void clear() {
        UserData user = new UserData("first","123password","first@first.com");
        UserData user2 = new UserData("second","123password","second@second.com");
        try {
            userDAO.createUser(user);
            userDAO.createUser(user2);
            userDAO.clear();
            Assertions.assertNull(userDAO.getUser("first"));
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
