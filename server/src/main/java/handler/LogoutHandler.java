package handler;

import request.LogoutRequest;
import response.LogoutResponse;
import service.ErrorException;
import service.LogoutService;
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
