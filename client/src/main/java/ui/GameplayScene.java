package ui;

import client.ClientData;
import exception.ErrorException;
import servermessage.ServerMessageObserver;
import facades.WebSocketFacade;

import java.util.HashMap;

public class GameplayScene extends Scene {
    private final SceneManager sceneManager;
    private final String serverURL;
    private final ServerMessageObserver observer;
    private final HashMap<String, UIFunction<String[],String>> validCommands;

    private ClientData data;

    public GameplayScene(SceneManager sceneManager, String serverURL, ServerMessageObserver observer, ClientData data) {
        this.sceneManager = sceneManager;
        this.serverURL = serverURL;
        this.observer = observer;
        this.data = data;

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

    //TODO implement methods in gameplay ui help
}
