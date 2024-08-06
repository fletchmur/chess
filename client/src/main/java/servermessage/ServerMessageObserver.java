package servermessage;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
     void notify(String message, ServerMessage.ServerMessageType type);
}
