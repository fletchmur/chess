package ui;

import java.util.Arrays;

import client.ClientData;
import exception.ErrorException;
import servermessage.ServerMessageObserver;
import request.*;
import facades.ServerFacade;


public class SceneManager {

    public enum State {
        SIGNED_IN,
        SIGNED_OUT,
        GAMEPLAY
    };

    private final ServerFacade facade;
    private State state = State.SIGNED_OUT;
    private final PreLoginScene preLoginScene;
    private final PostLoginScene postLoginScene;
    private  final GameplayScene gameplayScene;


    public SceneManager(String serverURL, ServerMessageObserver observer, ClientData clientData) throws ErrorException {

        facade = new ServerFacade(serverURL);
        preLoginScene = new PreLoginScene(this,facade,clientData);
        postLoginScene = new PostLoginScene(this,facade,serverURL,observer,clientData);
        gameplayScene = new GameplayScene(this,serverURL,observer,clientData);

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
            return switch (state) {
                case SIGNED_OUT -> preLoginScene.eval(cmd,params);
                case SIGNED_IN -> postLoginScene.eval(cmd,params);
                case GAMEPLAY -> gameplayScene.eval(cmd,params);
            };
        }
        catch (ErrorException e) {
            String errorFormat = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_RED;
            return errorFormat + e.getMessage();
        }
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    private String clear() throws ErrorException {
        facade.clear(new ClearRequest());
        String serverFormat = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
        return serverFormat + "cleared databases";
    }
}