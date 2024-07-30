package handler;

import request.JoinGameRequest;
import response.JoinGameResponse;
import exception.ErrorException;
import service.JoinGameService;
import spark.Route;

public class JoinGameHandler extends Handler<JoinGameRequest, JoinGameResponse> implements Route {
    @Override
    JoinGameResponse fulfillRequest(JoinGameRequest requestObj) throws ErrorException {
        return new JoinGameService().joinGame(requestObj,authToken);
    }

    @Override
    Class<?> getRequestType() {
        return JoinGameRequest.class;
    }
}
