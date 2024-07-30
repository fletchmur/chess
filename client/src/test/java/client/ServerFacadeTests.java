package client;

import org.junit.jupiter.api.*;
import request.*;
import response.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + Integer.toString(port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clearTest() {
        try {
           ClearResponse response = facade.clear(new ClearRequest());
           Assertions.assertInstanceOf(ClearResponse.class,response);
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

}
