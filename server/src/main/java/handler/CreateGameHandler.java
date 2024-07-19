package handler;

import request.CreateGameRequest;
import response.CreateGameResponse;
import service.CreateGameService;
import service.ErrorException;
import spark.Route;

public class CreateGameHandler extends Handler<CreateGameRequest, CreateGameResponse> implements Route {
    @Override
    CreateGameResponse fulfillRequest(CreateGameRequest requestObj) throws ErrorException {
        CreateGameService createGameService = new CreateGameService();
        return createGameService.createGame(requestObj);
    }

    @Override
    Class<?> getRequestType() {
        return CreateGameRequest.class;
    }
}
