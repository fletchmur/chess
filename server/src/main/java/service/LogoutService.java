package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.mysql.MySQLAuthDAO;
import response.LogoutResponse;
import exception.ErrorException;

public class LogoutService {
    AuthDAO authDAO = new MySQLAuthDAO();

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
