package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import response.ClearResponse;

public class ClearService {

    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    public ClearResponse clear()
    {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        return new ClearResponse();
    }
}
