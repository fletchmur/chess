package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import response.RegisterResponse;

public class RegisterService {

    private final UserDAO userDAO = new MemoryUserDAO();
    private final LoginService loginService = new LoginService();

    public RegisterResponse register(RegisterRequest registerRequest) throws ErrorException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if(username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new ErrorException(400,"bad request");
        }

        UserData user = userDAO.getUser(username);
        if(user != null) {
            throw new ErrorException(403,"already taken");
        }

        try {
            userDAO.createUser(new UserData(username,password,email));
        }
        catch(DataAccessException e)
        {
            throw new ErrorException(500,e.getMessage());
        }

        String authToken = loginService.login(new LoginRequest(username, password)).authToken();
        return new RegisterResponse(username,authToken);
    }
}
