package handler;

import service.AuthorizationService;
import service.ErrorException;

import spark.Request;
import spark.Response;
import spark.Route;

abstract class Handler<RequestType,ResponseType>  implements Route {

    protected final Serializer serializer = new Serializer();
    protected record ErrorMessage(String message) {};
    protected record ErrorResponse(int status, ErrorMessage message) {};

    //Because of generic type erasure, the type of the generics are lost at runtime
    //this is problematic for deserializing into the correct request objects
    //this cache object uses a technique called reflection to store the type of the request object
    //for this to work the constructors of the subclasses must explicitly pass an instance of the request type they want
    private final RequestType requestTypeCache;

    public Handler(RequestType requestType) {
        this.requestTypeCache = requestType;
    }
    //method to get the class from the cached request type
    private Class<?> getClazz() {
        return this.requestTypeCache.getClass();
    }
    private RequestType deserialize(Request httpRequest) {
        RequestType requestObj = (RequestType) serializer.deserialize(httpRequest.body(), getClazz());
        return requestObj;
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
