package server.handler;

import request.CreateGameRequest;
import response.CreateGameResponse;
import server.service.CreateGameService;
import exception.ErrorException;
import spark.Route;

public class CreateGameHandler extends Handler<CreateGameRequest, CreateGameResponse> implements Route {
    @Override
    CreateGameResponse fulfillRequest(CreateGameRequest requestObj) throws ErrorException {
        return new CreateGameService().createGame(requestObj);
    }

    @Override
    Class<?> getRequestType() {
        return CreateGameRequest.class;
    }
}
