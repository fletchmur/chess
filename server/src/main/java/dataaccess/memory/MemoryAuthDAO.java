package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    private static HashMap<String,AuthData> authData = new HashMap<>();
    @Override
    public String createAuth(AuthData authorization) throws DataAccessException {
       String authToken = authorization.authToken();
       if (authData.containsKey(authToken)) {
           throw new DataAccessException("user already has authorization");
       }
       authData.put(authToken,authorization);
       return authToken;
    }
    @Override
    public AuthData getAuth(String authToken) {
        return authData.get(authToken);
    }
    @Override
    public void updateAuth(String authToken, AuthData newData) throws DataAccessException {
        AuthData authorization = authData.get(authToken);
        if (authorization == null) {
            throw new DataAccessException("trying to update nonexistent authorization");
        }
        authData.put(authToken,newData);
    }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData authorization = authData.get(authToken);
        if (authorization == null) {
            throw new DataAccessException("trying to delete nonexistent authorization");
        }
        authData.remove(authToken);
    }
    @Override
    public void clear() {
        authData.clear();
    }
}
