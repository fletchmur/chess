package dataaccess;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class MySQLAuthDAOTests {

    MySQLUserDAO userDAO = new MySQLUserDAO();
    MySQLAuthDAO authDAO = new MySQLAuthDAO();

    @BeforeEach
    public void setUp() {
        try {
            authDAO.clear();
            userDAO.clear();
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createAuthorization() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));
            AuthData authData = new AuthData("123","fletch");

            String token = authDAO.createAuth(authData);
            Assertions.assertEquals("123",token);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createDuplicateAuth() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));
            AuthData authData = new AuthData("123","fletch");

            String token = authDAO.createAuth(authData);
            authDAO.createAuth(authData);
            Assertions.fail("created duplicate authorization");
        }
        catch (DataAccessException e) {
        }
    }

    @Test
    public void getAuthorization() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));
            AuthData authData = new AuthData("123","fletch");

            String token = authDAO.createAuth(authData);
            AuthData data = authDAO.getAuth(token);
            Assertions.assertEquals(authData,data);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void getFakeAuthorization() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));
            AuthData data = authDAO.getAuth("123");
            Assertions.assertNull(data);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void deleteAuthorization() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));
            AuthData authData = new AuthData("123","fletch");

            String token = authDAO.createAuth(authData);
            authDAO.deleteAuth(token);
            AuthData data = authDAO.getAuth(token);
            Assertions.assertNull(data);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void deleteFakeAuthorization() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));

            authDAO.deleteAuth("123");
            Assertions.fail("deleted fake authorization");
        }
        catch (DataAccessException e) {
        }
    }

    @Test
    public void clearTest() {
        try {
            userDAO.createUser(new UserData("fletch","abc","fletch@mail.com"));
            AuthData authData = new AuthData("123","fletch");
            String token = authDAO.createAuth(authData);
            authDAO.clear();
            AuthData data = authDAO.getAuth(token);
            Assertions.assertNull(data);
        }
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }


}
