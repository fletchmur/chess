package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    public int createGame(GameData gameData) throws DataAccessException;
    public GameData getGame(int gameID);
    public Collection<GameData> getAllGames();
    public void updateGame(int gameID, GameData newData);
    public void deleteGame(int gameID) throws DataAccessException;
    public void clear();
}
