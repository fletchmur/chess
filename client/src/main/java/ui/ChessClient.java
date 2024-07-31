package ui;

import java.util.Arrays;

import exception.ErrorException;
import request.*;
import model.GameData;
import response.ListGamesResponse;
import serverfacade.ServerFacade;


public class ChessClient {

    private enum State {
        SIGNED_IN,
        SIGNED_OUT
    };

    private final ServerFacade facade;
    private State state = State.SIGNED_OUT;

    private String setBoldBlue = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE;
    private String setFaintMagenta = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
    private String setBoldRed = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;
    private String successFormat = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_WHITE;


    public ChessClient(String serverURL) {
        facade = new ServerFacade(serverURL);
    }

    public String getStateString() {
        String color = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
        return switch(state) {
            case SIGNED_IN -> EscapeSequences.SET_TEXT_COLOR_WHITE + "[LOGGED_IN]";
            case SIGNED_OUT -> color + "[LOGGED_OUT]";
        };
    }

    public String eval(String line) {
        //TODO implement eval
        //This method should take in a line from the repl class, break it down into the command and parameters
        // It should then call the appropriate evaluation command and return a string that is the result of the command
        // being executed

        String[] tokens = line.split(" ");
        String cmd = tokens[0];
        String[] params = Arrays.copyOfRange(tokens,1,tokens.length);

        try {
            return switch(cmd) {
                case "list" -> list(params);
                case "create" -> create(params);
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                case "logout" -> logout(params);
                case "clear" -> clear();
                default -> help();
            };
        }
        catch (ErrorException e) {
            return setBoldRed + "[Error " + e.getErrorCode() + "] "+ EscapeSequences.SET_TEXT_FAINT + e.getMessage();
        }

    }

    private String list(String... params) throws ErrorException {
        if (params.length > 0) {
            return successFormat + "Expected Usage:" + setBoldBlue + " logout";
        }
        ListGamesRequest request = new ListGamesRequest();
        ListGamesResponse response = facade.listGames(request);
        GameData[] games = response.games();
        if (games.length == 0) {
            return successFormat + "No games found";
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < games.length-1; i++) {
            result.append(setBoldBlue).append(i+1).append(". ").append(successFormat).append(games[i].gameName()).append("\n");
        }
        result.append(setBoldBlue).append(games.length).append(". ").append(successFormat).append(games[games.length-1].gameName());
        return result.toString();
    }

    private String create(String... params) throws ErrorException {
        if (params.length != 1) {
            return successFormat + "Expected Usage:" + setBoldBlue + " create <NAME>";
        }

        String gameName = params[0];
        CreateGameRequest request = new CreateGameRequest(gameName);
        facade.createGame(request);
        return successFormat + "Created game " + gameName;
    }

    private String logout(String... params) throws ErrorException {
        if (params.length > 0) {
            return successFormat + "Expected Usage:" + setBoldBlue + " logout";
        }

        LogoutRequest request = new LogoutRequest();
        facade.logout(request);
        state = State.SIGNED_OUT;
        return successFormat + "Logout successful";
    }

    private String login(String... params) throws ErrorException {
        if(params.length != 2) {
            return successFormat + "Expected Usage:" + setBoldBlue + " login <USERNAME> <PASSWORD>";
        }

        String username = params[0];
        String password = params[1];

        LoginRequest loginRequest = new LoginRequest(username, password);
        facade.login(loginRequest);
        state = State.SIGNED_IN;
        return successFormat + "Successfully Logged In";
    }

    private String register(String... params) throws ErrorException {

        if(params.length != 3) {
            return  successFormat + "Expected Usage:" + setBoldBlue + " register <USERNAME> <PASSWORD> <EMAIL>";
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        facade.register(new RegisterRequest(username, password, email));
        state = State.SIGNED_IN;
        return successFormat + "Registered user: " + username;
    }

    private String quit() throws ErrorException {
        return switch (state) {
            case SIGNED_OUT ->  "quit";
            case SIGNED_IN -> throw new ErrorException(400, "Must logout before quitting program");
        };
    }

    private String help() {
        String prelude = setBoldBlue + "--POSSIBLE COMMANDS--\n";

        String helpString = switch (state) {
            case SIGNED_OUT ->
                    setBoldBlue + "register <USERNAME> <PASSWORD> <EMAIL>" + setFaintMagenta + " - to create an account\n" +
                    setBoldBlue + "login <USERNAME <PASSWORD>" + setFaintMagenta + " - login to an existing account\n" +
                    setBoldBlue + "quit" + setFaintMagenta + " - exit program\n" +
                    setBoldBlue + "help" + setFaintMagenta + " - show possible commands";
            case SIGNED_IN ->
                    setBoldBlue + "create <NAME>" + setFaintMagenta + " - create a new game\n" +
                    setBoldBlue + "list" + setFaintMagenta + " - list all games\n" +
                    setBoldBlue + "join <ID> <WHITE|BLACK>" + setFaintMagenta + " - join a game\n" +
                    setBoldBlue + "observe <ID>" + setFaintMagenta + " - spectate a game\n" +
                    setBoldBlue + "logout" + setFaintMagenta + " - when you are done\n" +
                    setBoldBlue + "help" + setFaintMagenta + " - show possible commands";
        };
        return prelude + helpString;
    }

    //TODO remove clear method and clear case in eval switch statement
    private String clear() throws ErrorException {
        facade.clear(new ClearRequest());
        return successFormat + "cleared databases";
    }
}
