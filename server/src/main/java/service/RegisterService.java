package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.RegisterRequest;
import response.RegisterResponse;

public class RegisterService {

    private final UserDAO userDAO = new MySQLUserDAO();
    private final LoginService loginService = new LoginService();

    public RegisterResponse register(RegisterRequest registerRequest) throws ErrorException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if(username == null || password == null || email == null) {
            throw new ErrorException(400,"bad request");
        }

        try {
            UserData user = userDAO.getUser(username);
            if(user != null) {
                throw new ErrorException(403,"already taken");
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            userDAO.createUser(new UserData(username,hashedPassword,email));
        }
        catch(DataAccessException e)
        {
            throw new ErrorException(500,e.getMessage());
        }

        String authToken = loginService.login(new LoginRequest(username, password)).authToken();
        return new RegisterResponse(username,authToken);
    }
}
