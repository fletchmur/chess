package handlers;

import requests.LoginRequest;
import responses.LoginResponse;
import spark.Route;


public class LoginHandler extends Handler<LoginRequest, LoginResponse> implements Route {

    @Override
    LoginResponse fulfillRequest(LoginRequest requestObj)
    {
        //TODO implement LoginHandler.fulfillRequest through the service
        return new LoginResponse("not implemented","not implemented");
    }

}
