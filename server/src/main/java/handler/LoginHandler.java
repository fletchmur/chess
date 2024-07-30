package handler;

import request.LoginRequest;
import response.LoginResponse;
import exception.ErrorException;
import spark.Route;
import service.LoginService;


public class LoginHandler extends Handler<LoginRequest, LoginResponse> implements Route {

    @Override
    LoginResponse fulfillRequest(LoginRequest requestObj) throws ErrorException {
        return new LoginService().login(requestObj);
    }

    @Override
    Class<?> getRequestType() {
        return LoginRequest.class;
    }


}
