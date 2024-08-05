package server.service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import response.ClearResponse;
import exception.ErrorException;

public class ClearService {

    UserDAO userDAO = new MySQLUserDAO();
    GameDAO gameDAO = new MySQLGameDAO();
    AuthDAO authDAO = new MySQLAuthDAO();

    public ClearResponse clear() throws ErrorException
    {
        try {
            gameDAO.clear();
            authDAO.clear();
            userDAO.clear();
            return new ClearResponse();
        }
        catch (Exception e) {
            throw new ErrorException(500,e.getMessage());
        }


    }
}
