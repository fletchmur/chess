package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public int createGame(GameData gameData) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public Collection<GameData> getAllGames() throws DataAccessException;
    public void updateGame(int gameID, GameData newData) throws DataAccessException;
    public void clear() throws DataAccessException;
}
