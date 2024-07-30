package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO extends MySQLDAO implements UserDAO  {

    @Override
    public String createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement,user.username(),user.password(),user.email());
            return user.username();
        }
        catch (DataAccessException e) {
            throw new DataAccessException("username taken");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT * FROM user WHERE username = ?";
        try(Connection connection = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1,username);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        String resultUsername = resultSet.getString("username");
                        String resultPassword = resultSet.getString("password");
                        String resultEmail = resultSet.getString("email");
                        return new UserData(resultUsername,resultPassword,resultEmail);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE user";
        executeUpdate(statement);
    }
}
