package handler;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import spark.Request;

public class Serializer {

    private Gson gson = new Gson();

    public Object deserialize(String httpRequest,Type requestType) {
        return gson.fromJson(httpRequest,requestType);
    }

    public String serialize(Object response)
    {
        return gson.toJson(response);
    }
}
