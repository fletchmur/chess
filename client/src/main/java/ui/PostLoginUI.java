package ui;

import chess.ChessBoard;
import chess.ChessGame;
import clientdata.ClientData;
import exception.ErrorException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import response.ListGamesResponse;
import facades.ServerFacade;

import java.util.HashMap;

public class PostLoginUI extends UI {

    private final ServerFacade facade;
    private final ChessClient client;
    private final HashMap<String, UIFunction<String[],String>> validCommands;
    private ClientData data;

    private final HashMap<Integer,Integer> listToGameID;

    public PostLoginUI(ChessClient client, ServerFacade facade, ClientData data) {
        this.facade = facade;
        this.client = client;
        this.data = data;

        listToGameID = new HashMap<>();

        validCommands = new HashMap<>();
        validCommands.put("observe",this::observe);
        validCommands.put("join",this::join);
        validCommands.put("list",this::list);
        validCommands.put("create",this::create);
        validCommands.put("logout",this::logout);
    }

    @Override
    String help() {
        String prelude = BOLD_FORMAT + "[HELP]\n";
        String helpString = BOLD_FORMAT + "create <NAME>" + SERVER_FORMAT + " - create a new game\n" +
                BOLD_FORMAT + "list" + SERVER_FORMAT + " - list all games\n" +
                BOLD_FORMAT + "join <ID> <WHITE|BLACK>" + SERVER_FORMAT + " - join a game\n" +
                BOLD_FORMAT + "observe <ID>" + SERVER_FORMAT + " - spectate a game\n" +
                BOLD_FORMAT + "logout" + SERVER_FORMAT + " - when you are done\n" +
                BOLD_FORMAT + "help" + SERVER_FORMAT + " - show possible commands";
        return prelude + helpString;
    }

    @Override
    HashMap<String, UIFunction<String[], String>> getValidMethods() {
        return validCommands;
    }

    private String observe(String... params) throws ErrorException {
        if(params.length != 1 || params[0].length() > 3) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " observe <ID>";
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
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " join <ID> <WHITE|BLACK>";
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
        if (params.length > 0) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " logout";
        }
        ListGamesRequest request = new ListGamesRequest();
        ListGamesResponse response = facade.listGames(request);
        GameData[] games = response.games();

        if (games.length == 0) {
            return SERVER_FORMAT + "No games found";
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < games.length-1; i++) {
            listToGameID.put(i + 1, games[i].gameID());
            result.append(BOLD_FORMAT).append(i+1).append(". ").append(SERVER_FORMAT).append(games[i].gameName()).append("\n");
            result.append(" <WHITE: ").append(games[i].whiteUsername()).append(">");
            result.append(" <BLACK: ").append(games[i].blackUsername()).append(">");
        }

        listToGameID.put(games.length, games[games.length-1].gameID());
        result.append(BOLD_FORMAT).append(games.length).append(". ").append(SERVER_FORMAT).append(games[games.length-1].gameName());
        result.append(" <WHITE: ").append(games[games.length-1].whiteUsername()).append(">");
        result.append(" <BLACK: ").append(games[games.length-1].blackUsername()).append(">");

        return result.toString();
    }

    private String create(String... params) throws ErrorException {
        if (params.length != 1) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " create <NAME>";
        }

        String gameName = params[0];
        CreateGameRequest request = new CreateGameRequest(gameName);
        facade.createGame(request);
        String gamesList = list();
        return SERVER_FORMAT + "Created game " + gameName + "\n\n" + gamesList;
    }

    private String logout(String... params) throws ErrorException {
        if (params.length > 0) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " logout";
        }

        LogoutRequest request = new LogoutRequest();
        facade.logout(request);
        client.setState(ChessClient.State.SIGNED_OUT);
        data.setUser(null);

        return SERVER_FORMAT + "Logout successful";
    }
}
