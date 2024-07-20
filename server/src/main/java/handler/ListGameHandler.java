package handler;

import request.ListGamesRequest;
import response.ListGamesResponse;
import service.ErrorException;
import service.ListGamesService;
import spark.Route;

public class ListGameHandler extends Handler<ListGamesRequest, ListGamesResponse> implements Route {

    @Override
    ListGamesResponse fulfillRequest(ListGamesRequest requestObj) throws ErrorException {
        return new ListGamesService().listGames();
    }

    @Override
    Class<?> getRequestType() {
        return ListGamesRequest.class;
    }
}
