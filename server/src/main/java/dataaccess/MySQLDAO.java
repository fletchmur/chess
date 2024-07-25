package dataaccess;

import service.ErrorException;

import java.sql.*;

public abstract class MySQLDAO {

    protected abstract String[] getCreateStatements();

    protected void configureDatabase() throws ErrorException {
        try {
            DatabaseManager.createDatabase();
            try(Connection connection = DatabaseManager.getConnection()) {
                for(String statement : getCreateStatements()) {
                    try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                throw new ErrorException(500,String.format("Unable to configure database: %s",e.getMessage()));
            }
        }
        catch(DataAccessException ex) {
            throw new ErrorException(500,String.format("Unable to create database: %s",ex.getMessage()));
        }
    }

    protected int executeUpdate(String statement, Object... params) throws SQLException, DataAccessException {
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
        }
    }
}
