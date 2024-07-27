package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import handler.Serializer;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


public class MySQLGameDAO extends MySQLDAO implements GameDAO {
    @Override
    protected String[] getCreateStatements() {
        return new String[] {
                """
                CREATE TABLE IF NOT EXISTS game (
                	gameID int NOT NULL AUTO_INCREMENT,
                	whiteUsername VARCHAR(255) NOT NULL,
                	blackUsername VARCHAR(255) NOT NULL,
                	gameName VARCHAR(255) NOT NULL,
                	game TEXT NOT NULL,
                	PRIMARY KEY(gameID),
                	FOREIGN KEY(whiteUsername) REFERENCES user(username),
                	FOREIGN KEY(blackUsername) REFERENCES user(username)
                );
                """
        };
    }

    private String serializeChessGame(ChessGame game) {
        return new Serializer().serialize(game);
    }
    private ChessGame deserializeChessGame(String gameJson) {
        //TODO implement chess game deserialization
        //TODO write a type adapter to deserialize chess game
        return new ChessGame();
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        String statement = "INSERT INTO game(whiteUsername,blackUsername,gameName,game) VALUES (?,?,?)";
        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        String serializedGame = serializeChessGame(game);
        int gameID = executeUpdate(statement, whiteUser, blackUser, gameName, game);
        return gameID;
    }

    private GameData unpackResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("gameID");
        String whiteUser = rs.getString("whiteUsername");
        String blackUser = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = deserializeChessGame(rs.getString("game"));
        return new GameData(id,whiteUser,blackUser,gameName,game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM games WHERE gameID=?";
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return unpackResultSet(rs);
                    }
                    return null;
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        String statement = "SELECT * FROM games";
        ArrayList<GameData> games = new ArrayList<>();
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement)) {
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        games.add(unpackResultSet(rs));
                    }
                    return games;
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, GameData newData) throws DataAccessException {
        String statement = "UPDATE game SET game = ? WHERE gameID = ?";
        String newDataJson = serializeChessGame(newData.game());
        executeUpdate(statement,newDataJson,gameID);
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE game";
        executeUpdate(statement);
    }
}
