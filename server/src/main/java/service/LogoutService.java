package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDAO;
import request.LogoutRequest;
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
