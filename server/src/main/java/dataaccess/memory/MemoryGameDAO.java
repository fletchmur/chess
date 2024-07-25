package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private static int currentID = 1;
    private static HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        Integer gameID = gameData.gameID();

        if(gameID == null) {
            gameID = currentID;
            currentID++;
        }

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
    public void clear() {
        games.clear();
        currentID = 1;
    }
}
