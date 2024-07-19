package handler;

import com.google.gson.reflect.TypeToken;
import request.LoginRequest;
import response.LoginResponse;
import response.RegisterResponse;
import service.ErrorException;
import spark.Request;
import spark.Route;
import service.LoginService;


public class LoginHandler extends Handler<LoginRequest, LoginResponse> implements Route {

    @Override
    LoginResponse fulfillRequest(LoginRequest requestObj) throws ErrorException
    {
        LoginService loginService = new LoginService();
        return loginService.login(requestObj);
    }

    @Override
    Class<?> getRequestType() {
        return LoginRequest.class;
    }


}
