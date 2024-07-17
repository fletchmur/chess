package server;

import handlers.LoginHandler;
import spark.*;
import handlers.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        /*
        TODO clear
        TODO register
        TODO login
        TODO logout
        TODO listGames
        TODO createGame
        TODO joinGame
         */
        Spark.post("/session",new LoginHandler());

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
