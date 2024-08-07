package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.ClientData;
import exception.ErrorException;
import facades.WebSocketFacade;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import response.ListGamesResponse;
import facades.ServerFacade;
import servermessage.ServerMessageObserver;

import java.util.HashMap;

public class PostLoginScene extends Scene {

    private final ServerFacade facade;
    private final SceneManager sceneManager;
    private final HashMap<String, UIFunction<String[],String>> validCommands;
    private ClientData data;

    private final String serverURL;
    private final ServerMessageObserver observer;

    private final HashMap<Integer,Integer> listToGameID;

    public PostLoginScene(SceneManager sceneManager, ServerFacade facade, String serverURL, ServerMessageObserver observer, ClientData data) {
        this.facade = facade;
        this.sceneManager = sceneManager;
        this.data = data;
        this.serverURL = serverURL;
        this.observer = observer;

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

        data.setTeamColor(ChessGame.TeamColor.WHITE);

        sceneManager.setState(SceneManager.State.GAMEPLAY);
        WebSocketFacade ws = new WebSocketFacade(serverURL,observer, data.getAuthToken());
        ws.connect(gameID);

        return EscapeSequences.FAINT_SERVER_FORMAT + "observing game..." + EscapeSequences.RESET_TEXT_COLOR;
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
        data.setTeamColor(teamColor);

        int joinID = Integer.parseInt(params[0]);
        boolean joinIDInRange = (joinID >= 1 && joinID <= listToGameID.keySet().size());
        Integer gameID = listToGameID.get(joinID);
        if (!joinIDInRange || gameID == null) {
            throw new ErrorException(400, "Invalid join ID");
        }

        JoinGameRequest request = new JoinGameRequest(teamColor,gameID);
        facade.joinGame(request);

        sceneManager.setState(SceneManager.State.GAMEPLAY);
        WebSocketFacade ws = new WebSocketFacade(serverURL,observer, data.getAuthToken());
        ws.connect(gameID);

        return EscapeSequences.FAINT_SERVER_FORMAT + "joining game..." + EscapeSequences.RESET_TEXT_COLOR;
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
            result.append(BOLD_FORMAT).append(i+1).append(". ").append(SERVER_FORMAT).append(games[i].gameName());
            result.append(" <WHITE: ").append(games[i].whiteUsername()).append(">");
            result.append(" <BLACK: ").append(games[i].blackUsername()).append(">");
            result.append("\n");
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

        sceneManager.setState(SceneManager.State.SIGNED_OUT);

        data.setAuthToken("");
        data.setUser(null);

        return SERVER_FORMAT + "Logout successful";
    }
}
