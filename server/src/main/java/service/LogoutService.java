package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDAO;
import response.LogoutResponse;

public class LogoutService {
    AuthDAO authDAO = new MemoryAuthDAO();

    public LogoutResponse logout(String authToken) throws ErrorException {
        try {
            authDAO.deleteAuth(authToken);
            return new LogoutResponse();
        }
        catch (DataAccessException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }
}
