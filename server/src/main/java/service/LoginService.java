package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import response.LoginResponse;

import java.util.UUID;

public class LoginService {

    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();

    public LoginResponse login(LoginRequest loginRequest) throws ErrorException
    {
        String requestUsername = loginRequest.username();
        String requestPassword = loginRequest.password();
        UserData registeredUser;

        try {
            registeredUser = userDAO.getUser(requestUsername);

            if(registeredUser == null || !requestUsername.equals(registeredUser.username()) || !requestPassword.equals(registeredUser.password()))
            {
                throw new ErrorException(401,"Unauthorized");
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
