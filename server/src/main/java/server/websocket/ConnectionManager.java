package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import serializer.Serializer;
import server.Server;
import server.logger.ServerLogger;
import spark.Request;
import spark.Response;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class ConnectionManager {

    private record  Pair<T,R>(T item1,R item2) {};

    private final ConcurrentHashMap<Pair<Integer,String>, Connection> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String rootClient, Session session) {
        Connection connection = new Connection(gameID,rootClient,session);
        Pair<Integer,String> idClientPair = new Pair<>(gameID,rootClient);
        connections.put(idClientPair, connection);
    }

    public void remove(Integer gameID,String rootClient) {
        Pair<Integer,String> idClientPair = new Pair<>(gameID,rootClient);
        connections.remove(idClientPair);
    }

    public Connection get(Integer gameID, String rootClient) {
        Pair<Integer,String> idClientPair = new Pair<>(gameID,rootClient);
        return connections.get(idClientPair);
    }

    public void transmit(Integer gameID, String client, ServerMessage message) throws IOException {
        assert connections.containsKey(new Pair<>(gameID,client));

        Connection connection = get(gameID,client);
        if(!connection.session.isOpen()) {
            remove(gameID,client);
            return;
        }

        connection.send(new Serializer().serialize(message));
    }

    public void broadcast(Integer gameID,String excludeClient, ServerMessage message) throws IOException {
        ArrayList<Connection> toRemove = new ArrayList<>();
        for (Connection connection : connections.values()) {
            if(connection.session.isOpen()) {
                if(Objects.equals(connection.gameID, gameID) && !connection.rootClient.equals(excludeClient)) {
                    log(connection.rootClient,message);
                    connection.send(new Serializer().serialize(message));
                }
            }
            else {
                toRemove.add(connection);
            }
        }

        //clean up closed connections from connection hashmap
        for(Connection connection : toRemove) {
            remove(connection.gameID,connection.rootClient);
        }
    }

    private void log(String rootClient, ServerMessage message) {
        if(message.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            ServerLogger.log(Level.FINE, String.format("Root Client: <" + rootClient + "> : " + ((Notification) message).getMessage()));
        }
    }
}
