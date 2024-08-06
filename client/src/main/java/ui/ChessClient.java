package ui;

import clientdata.ClientData;
import exception.ErrorException;
import servermessage.ServerMessageObserver;
import serializer.Serializer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class ChessClient implements ServerMessageObserver {

    private SceneManager sceneManager;
    private ClientData clientData = new ClientData(null,null);

    public ChessClient(String serverUrl) {
        try {
            sceneManager = new SceneManager(serverUrl,this,clientData);
        }
        catch (ErrorException e) {
            System.err.println(e.getMessage());
        }

    }

    public void run() {
        System.out.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + "♕ Welcome to Chess. Enter help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(result != "quit") {
            if(!sceneManager.getState().equals(SceneManager.State.GAMEPLAY)) {
                printPropmt();
            }
            String line = scanner.nextLine();
            result = sceneManager.eval(line);
            System.out.print(result);
            System.out.println();
        }
    }

    private void printPropmt() {
        System.out.println();
        System.out.print(sceneManager.getStateString() + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " >>> ");
    }

    @Override
    public void notify(String message, ServerMessage.ServerMessageType type) {

        String ERASE = EscapeSequences.ERASE_SCREEN;
        //TODO create a serverMessageProcessor class to handle processing messages from server
        if (type == ServerMessage.ServerMessageType.NOTIFICATION) {
            Notification notification = (Notification) new Serializer().deserialize(message, Notification.class);
            String msg = notification.getMessage();
            System.out.println(ERASE + "\n" + EscapeSequences.FAINT_SERVER_FORMAT + EscapeSequences.SET_TEXT_COLOR_YELLOW + msg);
        }
        else if (type == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage game = (LoadGameMessage) new Serializer().deserialize(message, LoadGameMessage.class);
            String msg = "LOADING GAME...";
            System.out.println(ERASE + "\n" + EscapeSequences.FAINT_SERVER_FORMAT + EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOAD] " + msg);
        }
        else if (type == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage error = (ErrorMessage) new Serializer().deserialize(message, ErrorMessage.class);
            String msg = error.getErrorMessage();
            System.out.println(ERASE + "\n" + EscapeSequences.FAINT_SERVER_FORMAT + EscapeSequences.SET_TEXT_COLOR_RED + msg);
        }
        printPropmt();
    }
}
