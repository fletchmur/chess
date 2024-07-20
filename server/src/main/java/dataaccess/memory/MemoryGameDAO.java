package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private static int currentID = 0;
    private static HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();
        if (games.containsKey(gameID)) {
            throw new DataAccessException("Game already exists");
        }
        games.put(gameID, gameData);
        return gameID;
    }
    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> getAllGames() {
        return games.values();
    }

    @Override
    public void updateGame(int gameID, GameData newData) {
        GameData game = getGame(gameID);
        games.put(gameID, newData);
    }
    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        if(!games.containsKey(gameID)) {
            throw new DataAccessException("trying to remove nonexistent game");
        }
        games.remove(gameID);
    }
    @Override
    public void clear() {
        games.clear();
        currentID = 0;
    }

    public static int newGameID() {
        return ++currentID;
    }
    public static int getCurrentID() {
        return currentID;
    }
}
