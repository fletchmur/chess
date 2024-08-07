package server.logger;

import server.Server;

import java.io.IOException;
import java.util.logging.*;

public class ServerLogger {
    private static java.util.logging.Logger logger;
    private static Level logLevel = Level.FINE;

    static {
        try {
            initLog(logLevel);
        } catch (IOException e) {
            System.out.println("Error initializing log: " + e.getMessage());
        }
    }

    private static void initLog(Level logLevel) throws IOException {
        LogManager.getLogManager().reset();
        logger = Logger.getLogger(Server.class.getName());
        logger.setLevel(logLevel);

        FileHandler fileHandler = new FileHandler("log.txt",false);
        fileHandler.setLevel(logLevel);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }
    
    
}
