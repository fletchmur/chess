package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLUserDAO;
import response.ClearResponse;

public class ClearService {

    UserDAO userDAO = new MySQLUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MySQLAuthDAO();

    public ClearResponse clear() throws ErrorException
    {
        try {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();
            return new ClearResponse();
        }
        catch (Exception e) {
            throw new ErrorException(500,e.getMessage());
        }


    }
}
