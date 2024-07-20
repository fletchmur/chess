package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.AuthData;

public class AuthorizationService {
    private final AuthDAO authDataAccess = new MemoryAuthDAO();

    public void authorize(String authToken) throws ErrorException
    {
        AuthData authorization = authDataAccess.getAuth(authToken);
        if(authorization == null)
        {
            throw new ErrorException(401,"unauthorized");
        }
    }

}
