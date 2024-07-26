package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO extends MySQLDAO implements AuthDAO {
    @Override
    protected String[] getCreateStatements() {
        return new String[]{
                """
               CREATE TABLE IF NOT EXISTS auth (
               	authToken VARCHAR(255) NOT NULL,
               	username VARCHAR(255) NOT NULL,
               	PRIMARY KEY(authToken),
               	FOREIGN KEY(username) REFERENCES user(username)
               );
                """
        };
    }

    @Override
    public String createAuth(AuthData data) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken,username) VALUES (?, ?)";
        executeUpdate(statement,data.authToken(),data.username());
        return data.authToken();
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT * FROM auth WHERE authToken = ?";
        AuthData authData = null;
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(statement)) {
                stmt.setString(1,authToken);
                try(ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        String token = rs.getString("authToken");
                        String username = rs.getString("username");
                        authData = new AuthData(token,username);
                    }
                    return authData;
                }
            }

        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData authData = getAuth(authToken);
        if(authData == null) {
            throw new DataAccessException("trying to delete nonexistent authorization");
        }
        String statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement,authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE auth";
        executeUpdate(statement);
    }
}
