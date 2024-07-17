package handlers;
import com.google.gson.reflect.TypeToken;
import services.AuthorizationService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;

abstract class Handler<RequestType,ResponseType>  implements Route {

    private Serializer serialzer;

    private RequestType deserialize(Request httpRequest) {
        //java has type erasure with generics, so the type token is used to preserve the type data at runtime
        Type requestType = new TypeToken<RequestType>(){}.getType();
        //serializer returns a generic Object class, since it has the request type it is safe to cast the return
        return (RequestType) serialzer.deserialize(httpRequest,requestType);
    }
    private String serialize(ResponseType response) {
        return serialzer.serialize(response);
    }


    // the authenticate method is on the abstract class so that each normal handler can authenticate if necessary
    protected boolean authenticate(String authToken) {
        AuthorizationService authorizer = new AuthorizationService();
        return authorizer.authorize(authToken);
    }
    //TODO create an unauthorized exception
    //this method will throw an unauthorized exception which will be handled in the get error handling method


    public Object handle(Request request, Response response) {
        RequestType requestObj = deserialize(request);
        try {
            ResponseType responseObj = fulfillRequest(requestObj);
            response.status(200);
            String serializedResponse = serialize(responseObj);
            response.body(serializedResponse);
            return serializedResponse;
        }
        catch(Exception e) {
            //TODO implement error messaging
            //ErrorResponse = handleErrors(e);
            //response.body(serializer.serialize(ErrorResponse)
            //return serializer.serialize(ErrorResponse)
            return e.getMessage();
        }
    }


    abstract ResponseType fulfillRequest(RequestType requestObj) throws Exception;
    //TODO create ErrorResponse Class
    //abstract ErrorResponse handleErrors(Exception e);

}
