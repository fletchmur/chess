package server.services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class AuthorizationService {
    private AuthDAO authDataAccess;

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
