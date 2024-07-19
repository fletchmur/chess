package dataaccess;

import model.GameData;
import model.UserData;

public interface GameDAO {
    public int createGame(GameData gameData) throws DataAccessException;
    public GameData getGame(int gameID);
    public void updateGame(int gameID, GameData newData) throws DataAccessException;
    public void deleteGame(int gameID) throws DataAccessException;
    public void clear();
}
