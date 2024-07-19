package handler;
import com.google.gson.reflect.TypeToken;

import service.AuthorizationService;
import service.ErrorException;

import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;

abstract class Handler<RequestType,ResponseType>  implements Route {

    private final Serializer serializer = new Serializer();
    protected record ErrorMessage(String message) {};
    protected record ErrorResponse(int status, ErrorMessage message) {};

    private RequestType deserialize(Request httpRequest) {
        //java has type erasure with generics, so the type token is used to preserve the type data at runtime
        Type requestType = new TypeToken<RequestType>(){}.getType();
        //serializer returns a generic Object class, since it has the request type it is safe to cast the return
        return (RequestType) serializer.deserialize(httpRequest.body(),requestType);
    }
    private String serialize(ResponseType response) {
        return serializer.serialize(response);
    }
    private String sendSuccess(ResponseType success,Response response) {
        response.status(200);
        String serializedResponse = serialize(success);
        response.body(serializedResponse);
        return serializedResponse;
    }
    private String sendError(ErrorResponse error, Response response) {
        response.status(error.status());
        response.body(serializer.serialize(error.message()));
        return serializer.serialize(error.message());
    }

    // the checkAuthorization method is on the abstract class so that each normal handler can authenticate if necessary
    protected void checkAuthorization(String authToken) throws ErrorException {
        AuthorizationService authorizer = new AuthorizationService();
        if(!authorizer.authorize(authToken)) {
            throw new ErrorException(401, "Unauthorized");
        }
    }

    public Object handle(Request httpRequest, Response httpResponse) {

        RequestType requestObj = deserialize(httpRequest);
        try {
            ResponseType successResponse = fulfillRequest(requestObj);
            return sendSuccess(successResponse, httpResponse);
        }
        catch(ErrorException e) {
            ErrorResponse error = new ErrorResponse(e.getErrorCode(),new ErrorMessage(e.getMessage()));
            return sendError(error,httpResponse);
        }
    }

    //ABSTRACT METHODS
    abstract ResponseType fulfillRequest(RequestType requestObj) throws ErrorException;

}
