package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
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
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = games.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("trying to access nonexistent game");
        }
        return gameData;
    }
    @Override
    public void updateGame(int gameID, GameData newData) throws DataAccessException {
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
    }
}
