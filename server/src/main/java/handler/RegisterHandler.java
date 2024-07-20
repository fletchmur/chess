package handler;

import request.RegisterRequest;
import response.RegisterResponse;
import service.ErrorException;
import service.RegisterService;
import spark.Route;

public class RegisterHandler extends Handler<RegisterRequest, RegisterResponse> implements Route {

    @Override
    RegisterResponse fulfillRequest(RegisterRequest requestObj) throws ErrorException {
        return new RegisterService().register(requestObj);
    }

    @Override
    Class<?> getRequestType() {
        return RegisterRequest.class;
    }
}
