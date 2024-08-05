package server.service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.mysql.MySQLAuthDAO;
import model.AuthData;
import exception.ErrorException;

public class AuthorizationService {
    private final AuthDAO authDataAccess = new MySQLAuthDAO();

    public void authorize(String authToken) throws ErrorException {
        try {
            AuthData authorization = authDataAccess.getAuth(authToken);
            if (authorization == null) {
                throw new ErrorException(401,"unauthorized");
            }
        }
        catch (DataAccessException e) {
            throw new ErrorException(500,e.getMessage());
        }



    }

}
