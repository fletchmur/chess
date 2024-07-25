package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.MySQLDAO;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import service.ErrorException;

import java.sql.SQLException;

public class MySQLUserDAO extends MySQLDAO implements UserDAO  {

    public MySQLUserDAO() throws ErrorException {
        //create the user table if it doesn't exist
        super.configureDatabase();
    }

    @Override
    protected String[] getCreateStatements() {
        return new String[]{
                """
                CREATE TABLE IF NOT EXISTS user (
                	username varchar(255) NOT NULL,
                	password varchar(255) NOT NULL,
                	email varchar(255) NOT NULL,
                	PRIMARY KEY (username)
                );
                """
        };
    }

    @Override
    public String createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement,user.username(),user.password(),user.email());
            return user.username();
        }
        catch (SQLException e) {
            throw new DataAccessException("username taken");
        }
    }

    @Override
    public UserData getUser(String username) {
        //TODO write method to get a user from the database
        return null;
    }

    @Override
    public void clear() {
        //TODO write method to clear everything from the user table in the database
    }
}
