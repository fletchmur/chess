package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import service.ErrorException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public UserData getUser(String username) throws DataAccessException {
        //TODO write method to get a user from the database
        String statement = "SELECT * FROM user WHERE username = ?";
        try(Connection connection = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1,username);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        String result_username = resultSet.getString("username");
                        String result_password = resultSet.getString("password");
                        String result_email = resultSet.getString("email");
                        return new UserData(result_username,result_password,result_email);
                    }
                    else {
                        throw new DataAccessException("user not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException{
        String statement = "TRUNCATE TABLE user";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
