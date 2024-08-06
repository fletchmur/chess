package server.websocket;

import chess.ChessBoard;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import serializer.Serializer;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = (UserGameCommand) new Serializer().deserialize(message, UserGameCommand.class);
        saveSession(command.getGameID(), session);

        switch(command.getCommandType()) {
            default -> test();
        }
    }

    private void saveSession(Integer gameID, Session session) {
        connections.add(gameID,session);
    }

    //TODO implement action methods and add them to the switch statement
    //TODO remove test action method
    private void test() throws IOException {
        Notification notification = new Notification("test notification");
        ErrorMessage error = new ErrorMessage("test error");
        LoadGameMessage load = new LoadGameMessage(new ChessBoard());
        connections.broadcast(1,notification);
    }
}
