package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

public class MySQLAuthDAO extends MySQLDAO implements AuthDAO {
    @Override
    public String createAuth(AuthData data) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    protected String[] getCreateStatements() {
        return new String[0];
    }
}
