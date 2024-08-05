package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import server.handler.Serializer;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        Connection connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeUsername, ServerMessage message) throws IOException {
        ArrayList<Connection> toRemove = new ArrayList<>();
        for (Connection connection : connections.values()) {
            if(connection.session.isOpen()) {
                if(!connection.username.equals(excludeUsername)) {
                    connection.send(new Serializer().serialize(message));
                }
            }
            else {
                toRemove.add(connection);
            }
        }

        //clean up closed connections from connection hashmap
        for(Connection connection : toRemove) {
            connections.remove(connection.username);
        }
    }
}
