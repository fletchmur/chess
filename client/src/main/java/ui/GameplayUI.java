package ui;

import exception.ErrorException;
import facades.ServerMessageObserver;
import facades.WebSocketFacade;

import java.util.HashMap;

public class GameplayUI extends UI {
    private final ChessClient client;
    private final String serverURL;
    private final ServerMessageObserver observer;
    private final HashMap<String, UIFunction<String[],String>> validCommands;

    public GameplayUI(ChessClient client, String serverURL, ServerMessageObserver observer) {
        this.client = client;
        this.serverURL = serverURL;
        this.observer = observer;
        validCommands = new HashMap<>();
        validCommands.put("wstest",this::webSocketTest);
    }


    @Override
    String help() {
        String prelude = BOLD_FORMAT + "[HELP]\n";
        String helpString =  BOLD_FORMAT + "redraw" + SERVER_FORMAT + " - redraws the board\n" +
                BOLD_FORMAT + "move <START POSITION> <END POSITION>" + SERVER_FORMAT + " - move the piece at start position to end position\n" +
                BOLD_FORMAT + "highlight <POSITION>" + SERVER_FORMAT + " - highlights the valid moves for piece at position\n" +
                BOLD_FORMAT + "leave" + SERVER_FORMAT + " - leave game\n" +
                BOLD_FORMAT + "resign" + SERVER_FORMAT + " - forfeit game to opponent\n" +
                BOLD_FORMAT + "help" + SERVER_FORMAT + " - show possible commands";
        return prelude + helpString;
    }

    @Override
    HashMap<String, UIFunction<String[], String>> getValidMethods() {
        return validCommands;
    }

    //TODO implement hashmap for methods in gameplay UI
    private String webSocketTest(String... params) throws ErrorException {
        WebSocketFacade ws = new WebSocketFacade(serverURL,observer);
        ws.test();
        return EscapeSequences.FAINT_SERVER_FORMAT + "sending basic command";
    }
}
