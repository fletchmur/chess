package server.websocket;

import chess.ChessBoard;
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
import server.service.AuthorizationService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = (UserGameCommand) new Serializer().deserialize(message, UserGameCommand.class);

            AuthorizationService authorizationService = new AuthorizationService();
            authorizationService.authorize(command.getAuthToken());

            String rootClient = getRootClientFromAuth(command.getAuthToken());
            saveSession(command.getGameID(), rootClient, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), rootClient);
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
        session.getRemote().sendString(new Serializer().serialize(error));
    }

    private void connect(Integer gameID,String rootClient) throws IOException,ErrorException {
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
            return " WHITE player";
        }
        else if(username.equals(gameData.blackUsername())) {
            return " BLACK player";
        }
        else {
            return " an observer";
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
