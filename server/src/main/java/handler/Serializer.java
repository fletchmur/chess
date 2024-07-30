package handler;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import service.ErrorException;

public class Serializer {

    private Gson gson = new Gson();

    public Object deserialize(String json, Type type)  {
        return gson.fromJson(json,type);
    }

    public String serialize(Object response)
    {
        return gson.toJson(response);
    }
}
