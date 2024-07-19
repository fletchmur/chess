package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;

public class AuthorizationService {
    private final AuthDAO authDataAccess = new MemoryAuthDAO();

    public boolean authorize(String authToken)
    {
        try
        {
            authDataAccess.getAuth(authToken);
            return true;
        }
        catch (DataAccessException e)
        {
            return false;
        }
    }

}
