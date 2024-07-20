package handler;

import service.AuthorizationService;
import service.ErrorException;

import spark.Request;
import spark.Response;
import spark.Route;

abstract class Handler<T, F>  implements Route {

    protected final Serializer serializer = new Serializer();
    protected record ErrorMessage(String message) {};
    protected record ErrorResponse(int status, ErrorMessage message) {};
    protected String authToken;

    private void authorize(Request httpRequest) throws ErrorException {
        String authToken = httpRequest.headers("authorization");
        if (authToken != null) {
            new AuthorizationService().authorize(authToken);
        }
    }
    private T deserialize(Request httpRequest) throws ErrorException {
        T requestObj = (T) serializer.deserialize(httpRequest.body(),getRequestType());
        return requestObj;
    }
    private String serialize(F response) {
        return serializer.serialize(response);
    }
    private String sendSuccess(F success, Response response) {
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

    @Override
    public Object handle(Request request, Response response) {
        try {
            authToken = request.headers("authorization");
            authorize(request);
            T requestObj = deserialize(request);
            F successResponse = fulfillRequest(requestObj);
            return sendSuccess(successResponse, response);
        }
        catch(ErrorException e) {
            ErrorResponse error = new ErrorResponse(e.getErrorCode(),new ErrorMessage(e.getMessage()));
            return sendError(error,response);
        }
    }

    //ABSTRACT METHODS
    abstract F fulfillRequest(T requestObj) throws ErrorException;
    //Because of generic type erasure in java, the type of the generics are lost at runtime
    //this is problematic for deserialization where we want to know the type so we can create the object.
    //To get around this we have the subclasses implement this method that returns an actual (non-generic) reference
    //to the requestType we want to deserialize into.
    abstract Class<?> getRequestType();
}
