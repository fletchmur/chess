package server;

import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        /*
        clear complete
        register complete
        login complete
        logout complete
        TODO listGames
        TODO createGame
        TODO joinGame
         */
        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session",new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/post", new CreateGameHandler());

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
