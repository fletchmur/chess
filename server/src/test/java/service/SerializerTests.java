package service;

import com.google.gson.reflect.TypeToken;
import serializer.Serializer;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;

public class SerializerTests {

    private final Serializer serializer = new Serializer();

    @Test
    public void emptyObject() {
        Assertions.assertEquals("{}",serializer.serialize(new Object()));
    }

    @Test
    public void loginRequestSerialize()
    {
        LoginRequest loginRequest = new LoginRequest("fletcher","1234");
        Assertions.assertEquals("{\"username\":\"fletcher\",\"password\":\"1234\"}",serializer.serialize(loginRequest));
    }

    @Test
    public void loginRequestDeserialize()
    {
        TypeToken<LoginRequest> loginRequestTypeToken = new TypeToken<>(){};
        String json = "{\"username\":\"fletcher\",\"password\":\"1234\"}";
        LoginRequest result = (LoginRequest) serializer.deserialize(json,loginRequestTypeToken.getType());
        Assertions.assertEquals(new LoginRequest("fletcher","1234"),result);
    }

    @Test
    public void registerRequestDeserialize()
    {
        TypeToken<RegisterRequest> requestTypeToken = new TypeToken<>(){};
        String json = "{\"username\":\"fletcher\",\"password\":\"1234\",\"email\":\"fletcher@gmail.com\"}";
        RegisterRequest result = (RegisterRequest) serializer.deserialize(json,requestTypeToken.getType());
        System.out.println(result.getClass());
        Assertions.assertEquals(new RegisterRequest("fletcher","1234","fletcher@gmail.com"),result);
    }
}
