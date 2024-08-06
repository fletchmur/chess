package facades;

import chess.ChessGame;
import exception.ErrorException;
import serializer.Serializer;
import servermessage.ServerMessageObserver;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
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

    public void test() throws ErrorException {
        try {
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT,"abc",1);
            this.session.getBasicRemote().sendText(new Serializer().serialize(cmd));
        }
        catch (IOException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }
}
