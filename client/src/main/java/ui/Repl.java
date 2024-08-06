package ui;

import exception.ErrorException;
import facades.ServerMessageObserver;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl implements ServerMessageObserver {

    private ChessClient client;

    public Repl(String serverUrl) {
        try {
            client = new ChessClient(serverUrl,this);
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
            printPropmt();
            String line = scanner.nextLine();
            result = client.eval(line);
            System.out.print(result);
            System.out.println();
        }
    }

    private void printPropmt() {
        System.out.println();
        System.out.print(client.getStateString() + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " >>> ");
    }

    @Override
    public void notify(ServerMessage message) {
        if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
           String msg =((Notification) message).getMessage();
            System.out.println("\n" + EscapeSequences.FAINT_SERVER_FORMAT + EscapeSequences.SET_TEXT_COLOR_YELLOW + "[Notification] " + msg);
        }
        printPropmt();
    }
}
