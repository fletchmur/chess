package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ClientData;
import exception.ErrorException;
import facades.WebSocketFacade;
import servermessage.ServerMessageObserver;
import websocket.commands.UserGameCommand;

import java.util.HashMap;
import java.util.Scanner;

public class GameplayScene extends Scene {


    private final SceneManager sceneManager;
    private final String serverURL;
    private final ServerMessageObserver observer;
    private final HashMap<String, UIFunction<String[],String>> validCommands;
    private final HashMap<String, ChessPosition> positionMap;

    private ClientData data;

    public GameplayScene(SceneManager sceneManager, String serverURL, ServerMessageObserver observer, ClientData data) {
        this.sceneManager = sceneManager;
        this.serverURL = serverURL;
        this.observer = observer;
        this.data = data;

        validCommands = new HashMap<>();
        validCommands.put("redraw", this::redraw);
        validCommands.put("move", this::move);
        validCommands.put("resign", this::resign);
        validCommands.put("leave",this::leave);

        positionMap = new HashMap<>();
        initPositionMap();

    }

    private void initPositionMap() {
        String[] letterArray = {"a","b","c","d","e","f","g","h"};
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                String positionString = String.format("%s%d", letterArray[j], i+1);
                positionMap.put(positionString, new ChessPosition(i+1, j+1));
            }
        }
    }

    @Override
    String help() {
        String prelude = BOLD_FORMAT + "[HELP]\n";
        String helpString =  BOLD_FORMAT + "redraw" + SERVER_FORMAT + " - redraws the board\n" +
                BOLD_FORMAT + "move <START> <END>" + SERVER_FORMAT + " - move the piece from start position to end position\n" +
                BOLD_FORMAT + "highlight <POSITION>" + SERVER_FORMAT + " - highlights the valid moves for piece at position\n" +
                BOLD_FORMAT + "leave" + SERVER_FORMAT + " - leave game\n" +
                BOLD_FORMAT + "resign" + SERVER_FORMAT + " - forfeit game to opponent\n" +
                BOLD_FORMAT + "help" + SERVER_FORMAT + " - show possible commands\n" +
                SERVER_FORMAT + "(POSITION FORMAT: a1, a2, b1, b2, ect..)";;
        return prelude + helpString;
    }

    @Override
    HashMap<String, UIFunction<String[], String>> getValidMethods() {
        return validCommands;
    }

    //TODO implement hashmap for methods in gameplay UI
    //TODO implement methods in gameplay ui help

    public String redraw(String... params) throws ErrorException {
        if(params.length != 0) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " redraw";
        }
        ChessBoardRenderer renderer = new ChessBoardRenderer(data.getGame().getBoard(),data.getTeamColor());
        return renderer.render();
    }

    public String move(String... params) throws ErrorException {
        if(params.length < 2) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " move <START> <END> (optional: <PROMOTION>)";
        }

        String startString = params[0];
        String endString = params[1];
        ChessPiece.PieceType promotion = null;

        ChessPosition startPosition = positionMap.get(startString);
        ChessPosition endPosition = positionMap.get(endString);

        //validate promotion
        ChessPiece startPiece = data.getGame().getBoard().getPiece(startPosition);
        if(params.length == 3) {
            if (startPiece.getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                promotion = switch (params[2]) {
                    case "ROOK", "rook", "r", "R" -> ChessPiece.PieceType.ROOK;
                    case "BISHOP", "bishop", "b", "B" -> ChessPiece.PieceType.BISHOP;
                    case "KNIGHT", "knight", "n", "N" -> ChessPiece.PieceType.KNIGHT;
                    case "QUEEN", "queen", "q", "Q" -> ChessPiece.PieceType.QUEEN;
                    default -> throw new ErrorException(500, "Invalid promotion");
                };
            }
            else {
                throw new ErrorException(500,"can only promote pawns");
            }

        }

        ChessMove move = new ChessMove(startPosition, endPosition, promotion);
        WebSocketFacade ws = new WebSocketFacade(serverURL,observer,data.getAuthToken());

        ws.move(data.getGameID(),move);
        return SERVER_FORMAT + "Moving " + move.movementString() + "...";
    }

    public String resign(String... params) throws ErrorException {
        if(params.length != 0) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " resign";
        }

        System.out.println(SERVER_FORMAT + "Are you sure you want to resign (y/n)?" + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        System.out.print(sceneManager.getStateString() + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " >>> ");

        Scanner scanner = new Scanner(System.in);
        String result = scanner.nextLine();
        if(result.equalsIgnoreCase("y") || result.equalsIgnoreCase("yes")) {
            WebSocketFacade ws = new WebSocketFacade(serverURL,observer,data.getAuthToken());
            ws.resign(data.getGameID());
            return SERVER_FORMAT + "Resigning..." + EscapeSequences.RESET_TEXT_COLOR;
        }

        return SERVER_FORMAT + "Canceling..." + EscapeSequences.RESET_TEXT_COLOR;
    }

    public String leave(String... params) throws ErrorException {
        if(params.length != 0) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " leave";
        }

        WebSocketFacade ws = new WebSocketFacade(serverURL,observer,data.getAuthToken());
        ws.leave(data.getGameID());
        data.setGameID(null);
        data.setTeamColor(null);
        sceneManager.setState(SceneManager.State.SIGNED_IN);

        return SERVER_FORMAT + "Leaving game..." + EscapeSequences.RESET_TEXT_COLOR;
    }
}
