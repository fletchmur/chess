package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.AuthData;

public class AuthorizationService {
    private final AuthDAO authDataAccess = new MemoryAuthDAO();

    public void authorize(String authToken) throws ErrorException
    {
        try {
            authDataAccess.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ErrorException(401,"unauthorized");
        }

    }

}
