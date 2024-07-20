package handler;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Serializer {

    private Gson gson = new Gson();

    public Object deserialize(String httpRequest, Type type) {
        if (type == null)
        {
            System.out.println("Subbmited null type to deserializer, did you implement getRequestType on the subclass?");
        }
        return gson.fromJson(httpRequest,type);
    }

    public String serialize(Object response)
    {
        return gson.toJson(response);
    }
}
