package handler;

import request.ClearRequest;
import response.ClearResponse;
import response.RegisterResponse;
import service.ClearService;
import service.ErrorException;
import spark.Route;

public class ClearHandler extends Handler<ClearRequest, ClearResponse> implements Route {

    public ClearHandler() {
        super(new ClearRequest());
    }

    @Override
    ClearResponse fulfillRequest(ClearRequest requestObj) throws ErrorException {
        ClearService clearService = new ClearService();
        return clearService.clear();
    }
}
