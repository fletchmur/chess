package ui;

import java.util.Arrays;
import java.util.HashMap;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ErrorException;
import request.*;
import model.GameData;
import response.ListGamesResponse;
import serverfacade.ServerFacade;


public class ChessClient {

    public enum State {
        SIGNED_IN,
        SIGNED_OUT
    };

    private final ServerFacade facade;
    private HashMap<Integer,Integer> listToGameID;
    private State state = State.SIGNED_OUT;
    private PreLoginUI preLoginUI;

    //FORMATING
    private final String boldServerFormat = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE;
    private final String errorFormat = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_RED;
    private final  String serverFormat = EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_MAGENTA;


    public ChessClient(String serverURL) {
        facade = new ServerFacade(serverURL);
        listToGameID = new HashMap<>();
        preLoginUI = new PreLoginUI(this,facade);

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
        // It should then call the appropriate evaluation command and return a string that is the result of the command
        // being executed

        String[] tokens = line.split(" ");
        String cmd = tokens[0];
        String[] params = Arrays.copyOfRange(tokens,1,tokens.length);


        try {

            if(this.getState() == State.SIGNED_OUT) {
                return preLoginUI.eval(cmd,params);
            }

            return switch(cmd) {
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "list" -> list(params);
                case "create" -> create(params);
                case "logout" -> logout(params);
                case "clear" -> clear();
                default -> help();
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


    private String observe(String... params) throws ErrorException {
        if(params.length != 1 || params[0].length() > 3) {
            return serverFormat + "Expected Usage:" + boldServerFormat + " observe <ID>";
        }

        int joinID = Integer.parseInt(params[0]);
        boolean joinIDInRange = (joinID >= 1 && joinID <= listToGameID.keySet().size());
        Integer gameID = listToGameID.get(joinID);
        if (!joinIDInRange || gameID == null) {
            throw new ErrorException(400, "Invalid join ID");
        }

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardRenderer renderer = new ChessBoardRenderer(board, ChessGame.TeamColor.WHITE);
        return renderer.render();
    }

    private String join(String... params) throws ErrorException {
        if(params.length != 2 || params[0].length() > 3) {
            return serverFormat + "Expected Usage:" + boldServerFormat + " join <ID> <WHITE|BLACK>";
        }

        ChessGame.TeamColor teamColor = switch(params[1]) {
            case "WHITE", "white", "w", "W" -> ChessGame.TeamColor.WHITE;
            case "BLACK", "black", "b", "B" -> ChessGame.TeamColor.BLACK;
            default -> throw new ErrorException(400, "Invalid team color");
        };
        int joinID = Integer.parseInt(params[0]);
        boolean joinIDInRange = (joinID >= 1 && joinID <= listToGameID.keySet().size());
        Integer gameID = listToGameID.get(joinID);
        if (!joinIDInRange || gameID == null) {
            throw new ErrorException(400, "Invalid join ID");
        }

        JoinGameRequest request = new JoinGameRequest(teamColor,gameID);
        facade.joinGame(request);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardRenderer renderer = new ChessBoardRenderer(board, teamColor);
        return renderer.render();
    }

    private String list(String... params) throws ErrorException {
        assertSignedIn();
        if (params.length > 0) {
            return serverFormat + "Expected Usage:" + boldServerFormat + " logout";
        }
        ListGamesRequest request = new ListGamesRequest();
        ListGamesResponse response = facade.listGames(request);
        GameData[] games = response.games();

        if (games.length == 0) {
            return serverFormat + "No games found";
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < games.length-1; i++) {
            listToGameID.put(i + 1, games[i].gameID());
            result.append(boldServerFormat).append(i+1).append(". ").append(serverFormat).append(games[i].gameName()).append("\n");
            result.append(" <WHITE: ").append(games[i].whiteUsername()).append(">");
            result.append(" <BLACK: ").append(games[i].blackUsername()).append(">");
        }

        listToGameID.put(games.length, games[games.length-1].gameID());
        result.append(boldServerFormat).append(games.length).append(". ").append(serverFormat).append(games[games.length-1].gameName());
        result.append(" <WHITE: ").append(games[games.length-1].whiteUsername()).append(">");
        result.append(" <BLACK: ").append(games[games.length-1].blackUsername()).append(">");

        return result.toString();
    }

    private String create(String... params) throws ErrorException {
        assertSignedIn();
        if (params.length != 1) {
            return serverFormat + "Expected Usage:" + boldServerFormat + " create <NAME>";
        }

        String gameName = params[0];
        CreateGameRequest request = new CreateGameRequest(gameName);
        facade.createGame(request);
        String gamesList = list();
        return serverFormat + "Created game " + gameName + "\n\n" + gamesList;
    }

    private String logout(String... params) throws ErrorException {
        assertSignedIn();
        if (params.length > 0) {
            return serverFormat + "Expected Usage:" + boldServerFormat + " logout";
        }

        LogoutRequest request = new LogoutRequest();
        facade.logout(request);
        state = State.SIGNED_OUT;
        return serverFormat + "Logout successful";
    }

    private String login(String... params) throws ErrorException {
        assertSignedOut();

        if(params.length != 2) {
            return serverFormat + "Expected Usage:" + boldServerFormat + " login <USERNAME> <PASSWORD>";
        }

        String username = params[0];
        String password = params[1];

        LoginRequest loginRequest = new LoginRequest(username, password);
        facade.login(loginRequest);
        state = State.SIGNED_IN;
        return serverFormat + "Successfully Logged In";
    }

    private String register(String... params) throws ErrorException {

        assertSignedOut();
        if(params.length != 3) {
            return  serverFormat + "Expected Usage:" + boldServerFormat + " register <USERNAME> <PASSWORD> <EMAIL>";
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        facade.register(new RegisterRequest(username, password, email));
        state = State.SIGNED_IN;
        return serverFormat + "Registered user: " + username;
    }

    private String quit() throws ErrorException {
        return switch (state) {
            case SIGNED_OUT ->  "quit";
            case SIGNED_IN -> throw new ErrorException(400, "Must logout before quitting program");
        };
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
        if(state == State.SIGNED_IN) {
            logout();
            state = State.SIGNED_OUT;
        }

        facade.clear(new ClearRequest());
        return serverFormat + "cleared databases";
    }
}
