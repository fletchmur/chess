package handler;

import request.LoginRequest;
import response.LoginResponse;
import service.ErrorException;
import spark.Route;
import service.LoginService;


public class LoginHandler extends Handler<LoginRequest, LoginResponse> implements Route {

    @Override
    LoginResponse fulfillRequest(LoginRequest requestObj) throws ErrorException
    {
        LoginService loginService = new LoginService();
        return loginService.login(requestObj);
    }

}
