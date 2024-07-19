package dataaccess;

import java.util.HashSet;

import model.AuthData;

public interface AuthDAO {

    public String createAuth(AuthData data) throws DataAccessException;
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void updateAuth(String authToken, AuthData data) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
    public void clear();

}
