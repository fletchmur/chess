package ui;

import java.util.Arrays;
import java.util.HashMap;

import exception.ErrorException;
import request.*;
import facades.ServerFacade;


public class ChessClient {

    public enum State {
        SIGNED_IN,
        SIGNED_OUT
    };

    private final ServerFacade facade;
    private HashMap<Integer,Integer> listToGameID;
    private State state = State.SIGNED_OUT;
    private PreLoginUI preLoginUI;
    private PostLoginUI postLoginUI;

    //FORMATING
    private final String boldServerFormat = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String errorFormat = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;
    private final  String serverFormat = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_MAGENTA;


    public ChessClient(String serverURL) {
        facade = new ServerFacade(serverURL);
        listToGameID = new HashMap<>();
        preLoginUI = new PreLoginUI(this,facade);
        postLoginUI = new PostLoginUI(this,facade);

    }

    public String getStateString() {
        String color = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
        return switch(state) {
            case SIGNED_IN -> EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED_IN]";
            case SIGNED_OUT -> color + "[LOGGED_OUT]";
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
                case SIGNED_OUT -> preLoginUI.eval(cmd,params);
                case SIGNED_IN -> postLoginUI.eval(cmd,params);
            };
        }
        catch (ErrorException e) {
            return errorFormat + "[Error " + e.getErrorCode() + "] "+ EscapeSequences.SET_TEXT_FAINT + e.getMessage();
        }

    }

    public void assertSignedIn() throws ErrorException {
        if(state == State.SIGNED_OUT) {
            throw new ErrorException(400, "Must sign in");
        }
    }

    public void assertSignedOut() throws ErrorException {
        if(state == State.SIGNED_IN) {
            throw new ErrorException(400, "Must sign out");
        }
    }

    public State getState() {
        return this.state;
    }
    public void setState(State state) {
        this.state = state;
    }


    private String help() {
        String prelude = boldServerFormat + "[HELP]\n";

        String helpString = switch (state) {
            case SIGNED_OUT ->
                    boldServerFormat + "register <USERNAME> <PASSWORD> <EMAIL>" + serverFormat + " - to create an account\n" +
                            boldServerFormat + "login <USERNAME <PASSWORD>" + serverFormat + " - login to an existing account\n" +
                            boldServerFormat + "quit" + serverFormat + " - exit program\n" +
                            boldServerFormat + "help" + serverFormat + " - show possible commands";
            case SIGNED_IN ->
                    boldServerFormat + "create <NAME>" + serverFormat + " - create a new game\n" +
                            boldServerFormat + "list" + serverFormat + " - list all games\n" +
                            boldServerFormat + "join <ID> <WHITE|BLACK>" + serverFormat + " - join a game\n" +
                            boldServerFormat + "observe <ID>" + serverFormat + " - spectate a game\n" +
                            boldServerFormat + "logout" + serverFormat + " - when you are done\n" +
                            boldServerFormat + "help" + serverFormat + " - show possible commands";
        };
        return prelude + helpString;
    }

    //TODO remove clear method and clear case in eval switch statement
    private String clear() throws ErrorException {
        facade.clear(new ClearRequest());
        return serverFormat + "cleared databases";
    }
}
