package servermessage;

import chess.ChessBoard;
import client.ClientData;
import serializer.Serializer;
import ui.ChessBoardRenderer;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class ServerMessageProcessor {

    private ClientData data;

    public ServerMessageProcessor(ClientData data) {
        this.data = data;
    }

    public String process(String message, ServerMessage.ServerMessageType type) {
        return switch (type) {
            case NOTIFICATION -> notification(message);
            case LOAD_GAME -> loadGame(message);
            case ERROR -> error(message);
        };
    }

    private String notification(String message) {
        Notification notification = (Notification) new Serializer().deserialize(message,Notification.class);
        String format = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_YELLOW;
        String msg = format + notification.getMessage();
        return msg + EscapeSequences.RESET_TEXT_COLOR;
    }

    private String loadGame(String message) {
        //TODO make sure that when you leave a game the client color data is reset
        LoadGameMessage loadGameMessage = (LoadGameMessage) new Serializer().deserialize(message,LoadGameMessage.class);
        ChessBoard board = loadGameMessage.getGameBoard();
        data.updateChessBoard(board);
        return new ChessBoardRenderer(board,data.getTeamColor()).render();
    }

    private String error(String message) {
        ErrorMessage errorMessage = (ErrorMessage) new Serializer().deserialize(message,ErrorMessage.class);
        String format = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_RED;
        String msg = format + errorMessage.getErrorMessage();
        return msg + EscapeSequences.RESET_TEXT_COLOR;
    }
}
