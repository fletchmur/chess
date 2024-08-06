package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import serializer.Serializer;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


public class MySQLGameDAO extends MySQLDAO implements GameDAO {

    private String serializeChessGame(ChessGame game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("trying to serialize null game");
        }
        return new Serializer().serialize(game);
    }
    private ChessGame deserializeChessGame(String gameJson) {
        ChessGame game = (ChessGame) new Serializer().deserialize(gameJson,ChessGame.class);
        return game;
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
    public int createGame(GameData gameData) throws DataAccessException {
        String statement = "INSERT INTO game(whiteUsername,blackUsername,gameName,game) VALUES (?,?,?,?)";
        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        String serializedGame = serializeChessGame(game);
        int gameID = executeUpdate(statement, whiteUser, blackUser, gameName, serializedGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM game WHERE gameID=?";
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
        String statement = "SELECT * FROM game";
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
        String statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
        String whiteUser = newData.whiteUsername();
        String blackUser = newData.blackUsername();
        String newDataJson = serializeChessGame(newData.game());
        executeUpdate(statement,whiteUser,blackUser,newDataJson,gameID);
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE game";
        executeUpdate(statement);
    }
}
