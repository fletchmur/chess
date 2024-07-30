package dataaccess.mysql;

import dataaccess.DataAccessException;

import java.sql.*;

public abstract class MySQLDAO {

    private static boolean databaseExists = false;
    private final String[][] createStatements = {
            {
                    """
                CREATE TABLE IF NOT EXISTS user (
                	username varchar(255) NOT NULL,
                	password varchar(255) NOT NULL,
                	email varchar(255) NOT NULL,
                	PRIMARY KEY (username)
                );
                """
            },
            {
                    """
               CREATE TABLE IF NOT EXISTS auth (
               	authToken VARCHAR(255) NOT NULL,
               	username VARCHAR(255) NOT NULL,
               	PRIMARY KEY(authToken)
               );
                """
            },
            {
                    """
                CREATE TABLE IF NOT EXISTS game (
                	gameID int NOT NULL AUTO_INCREMENT,
                	whiteUsername VARCHAR(255),
                	blackUsername VARCHAR(255),
                	gameName VARCHAR(255) NOT NULL,
                	game TEXT NOT NULL,
                	CONSTRAINT PK_gameID PRIMARY KEY(gameID)
                );
                """
            }

    };

    protected MySQLDAO() {
        if(!databaseExists) {
            for (String[] createStatement : createStatements) {
                configureDatabase(createStatement);
            }
            databaseExists = true;
        }
    }

    protected void configureDatabase(String[] createStatement) {
        try {
            DatabaseManager.createDatabase();
            try(Connection connection = DatabaseManager.getConnection()) {
                for(String statement : createStatement) {
                    try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(String.format("CRITICAL FAILURE CREATING TABLE: %s",e.getMessage()));
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(String.format("CRITICAL FAILURE CREATING DATABASE: %s",e.getMessage()));
        }
    }

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try(Connection connection = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for(int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, Types.NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
            catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
