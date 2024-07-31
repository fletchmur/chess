package server;

import handler.*;
import spark.*;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Server {

    private static Logger logger;
    static {
        try {
            initLog();
        } catch (IOException e) {
            System.out.println("Error initializing log: " + e.getMessage());
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

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
        logger.log(Level.INFO, String.format("[%s]%s - %d: %s", req.requestMethod(), req.pathInfo(), res.status(),res.body()));
    }

    private static void initLog() throws IOException {
        Level logLevel = Level.INFO;
        logger = Logger.getLogger(Server.class.getName());
        logger.setLevel(logLevel);

        FileHandler fileHandler = new FileHandler("log.txt",false);
        fileHandler.setLevel(logLevel);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
    }
}
