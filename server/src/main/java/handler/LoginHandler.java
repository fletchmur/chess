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

    public LoginHandler() {
        super(new LoginRequest("store type request", "a"));
    }
    @Override
    LoginResponse fulfillRequest(LoginRequest requestObj) throws ErrorException
    {
        LoginService loginService = new LoginService();
        return loginService.login(requestObj);
    }

    LoginRequest deserialize(Request httpRequest)
    {
        TypeToken<LoginRequest> typeToken = new TypeToken<>(){};
        LoginRequest requestObj = (LoginRequest) serializer.deserialize(httpRequest.body(), typeToken.getType());
        return requestObj;
    }

}
