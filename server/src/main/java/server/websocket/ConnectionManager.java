package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import serializer.Serializer;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        Connection connection = new Connection(gameID, session);
        connections.put(gameID, connection);
    }

    public void remove(Integer gameID) {
        connections.remove(gameID);
    }

    public void broadcast(String excludeUsername, ServerMessage message) throws IOException {
        ArrayList<Connection> toRemove = new ArrayList<>();
        for (Connection connection : connections.values()) {
            if(connection.session.isOpen()) {
                //TODO implement broadcasting so it excludes the correct username
                if(!"blah".equals(excludeUsername)) {
                    connection.send(new Serializer().serialize(message));
                }
            }
            else {
                toRemove.add(connection);
            }
        }

        //clean up closed connections from connection hashmap
        for(Connection connection : toRemove) {
            remove(connection.gameID);
        }
    }
}
