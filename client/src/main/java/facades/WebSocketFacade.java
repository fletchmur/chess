package facades;

import exception.ErrorException;
import serializer.Serializer;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws ErrorException {
        try {
            url = url.replace("http","ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this,socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = (ServerMessage) new Serializer().deserialize(message,ServerMessage.class);
                    if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        Notification notification = (Notification) new Serializer().deserialize(message,Notification.class);
                        observer.notify(notification);
                    }
                }
            });
        } catch (URISyntaxException | IOException | DeploymentException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void test() throws ErrorException {
        try {
            UserGameCommand testCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,"abc",1);
            this.session.getBasicRemote().sendText(new Serializer().serialize(testCommand));
        }
        catch (IOException e) {
            throw new ErrorException(500,e.getMessage());
        }
    }
}
