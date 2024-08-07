package server.websocket;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import exception.ErrorException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import serializer.Serializer;
import server.logger.ServerLogger;
import server.service.AuthorizationService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();

    //ON MESSAGE METHODS
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = (UserGameCommand) new Serializer().deserialize(message, UserGameCommand.class);
        try {

            AuthorizationService authorizationService = new AuthorizationService();
            authorizationService.authorize(command.getAuthToken());

            String rootClient = getRootClientFromAuth(command.getAuthToken());
            saveSession(command.getGameID(), rootClient, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), rootClient);
                case MAKE_MOVE -> makeMove(message,rootClient);
                case RESIGN -> resign(command.getGameID(), rootClient);
                default -> test(rootClient);
            }
        }
        catch (Exception e) {
            sendError(session,e.getMessage());
        }
    }

    private void saveSession(Integer gameID, String rootClient, Session session) {
        connections.add(gameID,rootClient,session);
    }
    private String getRootClientFromAuth(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        return authData.username();
    }
    private void sendError(Session session, String errorMessage) throws IOException {
        ErrorMessage error = new ErrorMessage(errorMessage);
        ServerLogger.log(Level.FINE,String.format("Session: <" + session.getRemote().toString() + "> : "  + error.getErrorMessage()));
        session.getRemote().sendString(new Serializer().serialize(error));
    }

    //CONNECT METHODS
    private void connect(Integer gameID,String rootClient) throws IOException, ErrorException {
        try {
            GameData gameData = gameDAO.getGame(gameID);
            ChessBoard board = gameData.game().getBoard();
            LoadGameMessage loadGameMessage = new LoadGameMessage(board);
            connections.transmit(gameID,rootClient,loadGameMessage);

            String participantString = getParticipantString(rootClient,gameData);

            Notification rootJoinedNotification = new Notification(rootClient + " joined game as " + participantString);
            connections.broadcast(gameID,rootClient,rootJoinedNotification);
        }
        catch (DataAccessException e) {
            throw new ErrorException(500, e.getMessage());
        }
    }

    private String getParticipantString(String username,GameData gameData) {
        if(username.equals(gameData.whiteUsername())) {
            return "WHITE player";
        }
        else if(username.equals(gameData.blackUsername())) {
            return "BLACK player";
        }
        else {
            return "an observer";
        }
    }

    //MOVE METHODS
    private void makeMove(String message,String rootClient) throws ErrorException, IOException {
        try {
            MakeMoveCommand cmd = (MakeMoveCommand) new Serializer().deserialize(message, MakeMoveCommand.class);
            GameData gameData = gameDAO.getGame(cmd.getGameID());

            ChessGame game = gameData.game();
            ChessMove move = cmd.getMove();

            validateMoveOwnPiece(gameData,rootClient,move);

            game.makeMove(move);

            GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(gameData.gameID(), newGameData);

            LoadGameMessage loadMsg = new LoadGameMessage(newGameData.game().getBoard());
            connections.broadcast(gameData.gameID(),"",loadMsg);
            Notification moveNotification = new Notification(rootClient + " moved " + move.movementString());
            connections.broadcast(gameData.gameID(),rootClient,moveNotification);

            //using logical short-circuiting to make sure only one of these messages is sent
            if(staleMateNotification(gameData) || checkMateNotification(gameData) || checkNotification(gameData)) {
                return;
            }
        }
        catch (DataAccessException | InvalidMoveException e) {
            throw new ErrorException(500, e.getMessage());
        }

    }

    private void validateMoveOwnPiece(GameData gameData,String rootClient,ChessMove move) throws ErrorException {
        ChessGame game = gameData.game();

        ChessGame.TeamColor myColor = null;

        boolean whiteTeam = rootClient.equals(gameData.whiteUsername());
        boolean blackTeam = rootClient.equals(gameData.blackUsername());

        if(whiteTeam) {
            myColor = ChessGame.TeamColor.WHITE;
        }
        else if(blackTeam) {
            myColor = ChessGame.TeamColor.BLACK;
        }

        if(myColor == null) {
            throw new ErrorException(500,"Can't move pieces when observing");
        }
        ChessPiece pieceToMove = game.getBoard().getPiece(move.getStartPosition());
        if(!myColor.equals(pieceToMove.getTeamColor())) {
            throw new ErrorException(500,"Can't move opponent's piece");
        }
    }
    private boolean staleMateNotification(GameData gameData) throws IOException {
        ChessGame game = gameData.game();

        boolean stalemateWhite = game.isInStalemate(ChessGame.TeamColor.WHITE);
        boolean stalemateBlack = game.isInStalemate(ChessGame.TeamColor.BLACK);

        if(stalemateWhite && stalemateBlack) {
            Notification stalemateNotification = new Notification("Game is in stalemate!");
            connections.broadcast(gameData.gameID(), "",stalemateNotification);
            return true;
        }
        return false;
    }
    private boolean checkMateNotification(GameData gameData) throws IOException {
        ChessGame game = gameData.game();

        boolean whiteCheckMate = game.isInCheckmate(ChessGame.TeamColor.WHITE);
        boolean blackCheckMate = game.isInCheckmate(ChessGame.TeamColor.BLACK);

        if(whiteCheckMate) {
            Notification whiteNotification = new Notification(gameData.whiteUsername() + " is in checkmate");
            connections.broadcast(gameData.gameID(), "",whiteNotification);
            return true;
        }
        else if(blackCheckMate) {
            Notification blackNotification = new Notification(gameData.blackUsername() + " is in checkmate");
            connections.broadcast(gameData.gameID(),"",blackNotification);
            return true;
        }
        return false;
    }
    private boolean checkNotification(GameData gameData) throws IOException {
        ChessGame game = gameData.game();

        boolean whiteCheck = game.isInCheck(ChessGame.TeamColor.WHITE);
        boolean blackCheck = game.isInCheck(ChessGame.TeamColor.BLACK);

        if(whiteCheck) {
            Notification whiteCheckNotification = new Notification(gameData.whiteUsername() + " is in check");
            connections.broadcast(gameData.gameID(), "",whiteCheckNotification);
            return true;
        }
        else if(blackCheck) {
            Notification blackCheckNotification = new Notification(gameData.blackUsername() + " is in check");
            connections.broadcast(gameData.gameID(), "",blackCheckNotification);
            return true;
        }
        return false;
    }

    //RESIGN METHODS
    public void resign(Integer gameID,String rootClient) throws IOException, ErrorException {
        try {
            GameData gameData = gameDAO.getGame(gameID);

            if(!rootClient.equals(gameData.whiteUsername()) && !rootClient.equals(gameData.blackUsername())) {
                throw new ErrorException(500,"Observers can't resign game");
            }
            if(gameData.game().isGameOver()) {
                throw new ErrorException(500,"Game is over, can't resign");
            }

            gameData.game().resign();
            gameDAO.updateGame(gameID, gameData);
            Notification notification = new Notification(rootClient + " resigned the game");
            connections.broadcast(gameData.gameID(), "",notification);
        }
        catch (DataAccessException e) {
            throw new ErrorException(500, e.getMessage());
        }
    }

    //TODO implement action methods and add them to the switch statement
    //TODO remove test action method
    private void test(String rootClient) throws IOException {
        Notification notification = new Notification("notifying " + rootClient);
        ErrorMessage error = new ErrorMessage("test error");
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        LoadGameMessage load = new LoadGameMessage(board);
        connections.broadcast(1,"",load);
    }
}
