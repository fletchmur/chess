package handler;

import request.ClearRequest;
import response.ClearResponse;
import service.ClearService;
import exception.ErrorException;
import spark.Route;

public class ClearHandler extends Handler<ClearRequest, ClearResponse> implements Route {

    @Override
    ClearResponse fulfillRequest(ClearRequest requestObj) throws ErrorException {
        return new ClearService().clear();
    }

    @Override
    Class<?> getRequestType() {
        return ClearRequest.class;
    }


}
