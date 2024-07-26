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
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = authData.get(authToken);
        if (auth == null) {
            throw new DataAccessException("trying to access nonexistent auth");
        }
        return auth;
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
