package facades;

import chess.ChessGame;
import chess.ChessMove;
import exception.ErrorException;
import serializer.Serializer;
import servermessage.ServerMessageObserver;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private final Session session;
    ServerMessageObserver observer;
    private final String authToken;

    public WebSocketFacade(String url, ServerMessageObserver observer,String authToken) throws ErrorException {
        try {
            this.authToken = authToken;
            url = url.replace("http","ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this,socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    observer.notify(message,getMessageType(message));
                }

                private ServerMessage.ServerMessageType getMessageType(String message) {
                    ServerMessage serverMessage = (ServerMessage) new Serializer().deserialize(message,ServerMessage.class);
                    return serverMessage.getServerMessageType();
                }
            });
        } catch (URISyntaxException | IOException | DeploymentException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(Integer gameID) throws ErrorException {
        try {
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID);
            this.session.getBasicRemote().sendText(new Serializer().serialize(cmd));
        }
        catch (IOException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }

    public void move(Integer gameID, ChessMove move) throws ErrorException {
        try {
            MakeMoveCommand cmd = new MakeMoveCommand(authToken,gameID,move);
            this.session.getBasicRemote().sendText(new Serializer().serialize(cmd));
        }
        catch (IOException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }

    public void leave(Integer gameID) throws ErrorException {
        try {
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE,authToken,gameID);
            this.session.getBasicRemote().sendText(new Serializer().serialize(cmd));
        }
        catch (IOException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }

    public void resign(Integer gameID) throws ErrorException {
        try {
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN,authToken,gameID);
            this.session.getBasicRemote().sendText(new Serializer().serialize(cmd));
        }
        catch (IOException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }
}
