package dataaccess.mysql;

import dataaccess.DataAccessException;
import service.ErrorException;

import java.sql.*;

public abstract class MySQLDAO {

    protected abstract String[] getCreateStatements();
    private boolean dataBaseExists;

    protected MySQLDAO() {
        configureDatabase();
    }

    protected void configureDatabase() {
        if (dataBaseExists) {return;}
        try {
            DatabaseManager.createDatabase();
            try(Connection connection = DatabaseManager.getConnection()) {
                for(String statement : getCreateStatements()) {
                    try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
                dataBaseExists = true;
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
