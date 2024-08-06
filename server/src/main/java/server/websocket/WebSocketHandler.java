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
        saveSession(command.getGameID(),command.getRootClient(), session);

        switch(command.getCommandType()) {
            default -> test(command.getRootClient());
        }
    }

    private void saveSession(Integer gameID, String rootClient, Session session) {
        connections.add(gameID,rootClient,session);
    }

    //TODO implement action methods and add them to the switch statement
    //TODO remove test action method
    private void test(String rootClient) throws IOException {
        Notification notification = new Notification("notifying " + rootClient);
        ErrorMessage error = new ErrorMessage("test error");
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        LoadGameMessage load = new LoadGameMessage(board);
        connections.broadcast(1,"",load);
    }
}
