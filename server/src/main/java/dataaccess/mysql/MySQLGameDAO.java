package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.List;

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

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return List.of();
    }

    @Override
    public void updateGame(int gameID, GameData newData) {

    }

    @Override
    public void clear() {

    }
}
