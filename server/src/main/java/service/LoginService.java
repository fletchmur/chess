package service;

import dataaccess.*;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import response.LoginResponse;

import java.util.UUID;

public class LoginService {

    AuthDAO authDAO = new MySQLAuthDAO();
    UserDAO userDAO = new MySQLUserDAO();

    public LoginResponse login(LoginRequest loginRequest) throws ErrorException
    {
        String requestUsername = loginRequest.username();
        String requestPassword = loginRequest.password();
        UserData registeredUser;

        try {
            registeredUser = userDAO.getUser(requestUsername);

            if (registeredUser == null) {
                throw new ErrorException(401,"unauthorized");
            }

            boolean userNameMatches = requestUsername.equals(registeredUser.username());
            boolean passwordMatches = BCrypt.checkpw(requestPassword, registeredUser.password());
            if(!userNameMatches|| !passwordMatches)
            {
                throw new ErrorException(401,"unauthorized");
            }

            //found the username and password, now we can create an auth token and return it
            String authToken = authDAO.createAuth(new AuthData(UUID.randomUUID().toString(),requestUsername));
            return new LoginResponse(requestUsername,authToken);
        }
        catch(DataAccessException e) {
            //tried to get the info of a user that doesn't exist
            throw new ErrorException(500,e.getMessage());
        }
    }
}
