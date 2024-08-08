package client;

import chess.ChessGame;
import exception.ErrorException;
import servermessage.ServerMessageObserver;
import servermessage.ServerMessageProcessor;
import ui.EscapeSequences;
import ui.SceneManager;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class ChessClient implements ServerMessageObserver {

    private SceneManager sceneManager;
    private ClientData clientData = new ClientData(new ChessGame(),"");

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
            printPropmt();
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

        ServerMessageProcessor processor = new ServerMessageProcessor(clientData);
        String msg = processor.process(message,type);
        System.out.println("\n" + msg);

        printPropmt();
    }
}
