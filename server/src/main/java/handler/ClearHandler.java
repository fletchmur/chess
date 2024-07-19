package handler;

import request.ClearRequest;
import response.ClearResponse;
import service.ClearService;
import service.ErrorException;
import spark.Route;

public class ClearHandler extends Handler<ClearRequest, ClearResponse> implements Route {

    @Override
    ClearResponse fulfillRequest(ClearRequest requestObj) throws ErrorException {
        ClearService clearService = new ClearService();
        return clearService.clear();
    }
}
