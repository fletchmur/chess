package ui;

import java.util.Arrays;

import clientdata.ClientData;
import exception.ErrorException;
import facades.ServerMessageObserver;
import facades.WebSocketFacade;
import request.*;
import facades.ServerFacade;


public class ChessClient {

    private ClientData clientData = new ClientData(null,null);

    public enum State {
        SIGNED_IN,
        SIGNED_OUT,
        GAMEPLAY
    };

    private final ServerFacade facade;
    private State state = State.SIGNED_OUT;
    private final PreLoginUI preLoginUI;
    private final PostLoginUI postLoginUI;
    private  final GameplayUI gameplayUI;


    public ChessClient(String serverURL, ServerMessageObserver observer) throws ErrorException {

        facade = new ServerFacade(serverURL);
        preLoginUI = new PreLoginUI(this,facade,clientData);
        postLoginUI = new PostLoginUI(this,facade,clientData);
        gameplayUI = new GameplayUI(this,serverURL,observer,clientData);

    }

    public String getStateString() {
        String color = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
        return switch(state) {
            case SIGNED_IN -> EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED_IN]";
            case SIGNED_OUT -> color + "[LOGGED_OUT]";
            case GAMEPLAY -> EscapeSequences.SET_TEXT_COLOR_GREEN + "[GAMEPLAY]";

        };
    }

    public String eval(String line) {
        //This method should take in a line from the repl class, break it down into the command and parameters
        // It should then call the UI class which will hand the command and parameters to correct method

        String[] tokens = line.split(" ");
        String cmd = tokens[0];
        String[] params = Arrays.copyOfRange(tokens,1,tokens.length);
        try {
            if(cmd.equals("clear")) {
                return clear();
            }
            if(cmd.equals("ws")) {
                state = State.GAMEPLAY;
            }

            return switch (state) {
                case SIGNED_OUT -> preLoginUI.eval(cmd,params);
                case SIGNED_IN -> postLoginUI.eval(cmd,params);
                case GAMEPLAY -> gameplayUI.eval(cmd,params);
            };
        }
        catch (ErrorException e) {
            String errorFormat = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;
            return errorFormat + "[Error " + e.getErrorCode() + "] "+ EscapeSequences.SET_TEXT_FAINT + e.getMessage();
        }

    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    //TODO remove clear method and clear checking if statement
    private String clear() throws ErrorException {
        facade.clear(new ClearRequest());
        String serverFormat = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
        return serverFormat + "cleared databases";
    }
}