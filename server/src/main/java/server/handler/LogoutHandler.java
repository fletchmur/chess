package server.handler;

import request.LogoutRequest;
import response.LogoutResponse;
import exception.ErrorException;
import server.service.LogoutService;
import spark.Route;

public class LogoutHandler extends Handler<LogoutRequest, LogoutResponse> implements Route {
    @Override
    LogoutResponse fulfillRequest(LogoutRequest requestObj) throws ErrorException {
        return new LogoutService().logout(authToken);
    }

    @Override
    Class<?> getRequestType() {
        return LogoutRequest.class;
    }
}
