package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;

public class AuthorizationService {
    private final AuthDAO authDataAccess = new MemoryAuthDAO();

    public boolean authorize(String authToken)
    {
        AuthData authorization = authDataAccess.getAuth(authToken);
        return authorization != null;
    }

}
