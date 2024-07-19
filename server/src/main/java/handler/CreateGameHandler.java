package handler;

import request.CreateGameRequest;
import service.ErrorException;
import spark.Route;

public class CreateGameHandler extends Handler<CreateGameRequest, CreateGameResponse> Route {
    @Override
    CreateGameResponse fulfillRequest(CreateGameRequest requestObj) throws ErrorException {
        return null;
    }

    @Override
    Class<?> getRequestType() {
        return null;
    }
}
