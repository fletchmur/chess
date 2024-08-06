package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public Integer gameID;
    public Session session;
    public String rootClient;

    public Connection(Integer gameID,String rootClient, Session session) {
        this.gameID = gameID;
        this.session = session;
        this.rootClient = rootClient;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
