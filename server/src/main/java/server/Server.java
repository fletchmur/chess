package server;

import server.handler.*;
import server.logger.ServerLogger;
import server.websocket.WebSocketHandler;
import spark.*;

import java.util.logging.*;

public class Server {

    private final WebSocketHandler webSocketHandler = new WebSocketHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.webSocket("/ws", webSocketHandler);

        Spark.after(this::log);

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

    private void log(Request req, Response res) {
        ServerLogger.log(Level.INFO, String.format("[%s]%s - %d: %s", req.requestMethod(), req.pathInfo(), res.status(),res.body()));
    }
}
