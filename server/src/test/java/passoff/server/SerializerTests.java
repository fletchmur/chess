package passoff.server;

import com.google.gson.reflect.TypeToken;
import handler.Serializer;
import org.junit.jupiter.api.*;
import request.LoginRequest;

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
        TypeToken<LoginRequest> loginRequestTypeToken = new TypeToken<LoginRequest>(){};
        String json = "{\"username\":\"fletcher\",\"password\":\"1234\"}";
        Assertions.assertEquals(new LoginRequest("fletcher","1234"),serializer.deserialize(json,loginRequestTypeToken.getType()));
    }
}
