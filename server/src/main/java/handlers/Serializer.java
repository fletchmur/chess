package handlers;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import spark.Request;

public class Serializer {

    private Gson gson;

    public Object deserialize(Request httpRequest,Type requestType) {
        return gson.fromJson(httpRequest.body(),requestType);
    }

    public String serialize(Object response)
    {
        return gson.toJson(response);
    }
}
