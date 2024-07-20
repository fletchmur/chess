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
    protected String authToken;

    private void authorize(Request httpRequest) throws ErrorException {
        String authToken = httpRequest.headers("authorization");
        if (authToken != null) {
            new AuthorizationService().authorize(authToken);
        }
    }
    private String httpToJson(Request httpRequest) {
        //inject the authToken into the json of the body to construct the correct Request object
        String authToken = httpRequest.headers("authorization");

        String body = httpRequest.body();

        if(!body.isEmpty() && authToken != null) {
            body = body.substring(1, body.length() - 1);
        }

        String comma = !body.isEmpty() ? "," : "";

        String json = authToken != null ? "{authToken: " + authToken + comma + body + "}" : body;
        return json;
    }
    private RequestType deserialize(Request httpRequest) {
        String json = httpToJson(httpRequest);
        RequestType requestObj = (RequestType) serializer.deserialize(httpRequest.body(),getRequestType());
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

    public Object handle(Request httpRequest, Response httpResponse) {
        try {
            authToken = httpRequest.headers("authorization");
            authorize(httpRequest);
            RequestType requestObj = deserialize(httpRequest);
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
    //Because of generic type erasure in java, the type of the generics are lost at runtime
    //this is problematic for deserialization where we want to know the type so we can create the object.
    //To get around this we have the subclasses implement this method that returns an actual (non-generic) reference
    //to the requestType we want to deserialize into.
    abstract Class<?> getRequestType();
}
