package server;

import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session",new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.get("/game", new ListGameHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.put("/game",new JoinGameHandler());

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
