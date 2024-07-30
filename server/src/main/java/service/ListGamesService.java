package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import response.ListGamesResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListGamesService {

    GameDAO gameDAO = new MySQLGameDAO();

    public ListGamesResponse listGames() throws ErrorException {
        try {
            Collection<GameData> games = gameDAO.getAllGames();
            List<GameData> gamesList = new ArrayList<>(games);
            GameData[] gameArray = gamesList.toArray(new GameData[gamesList.size()]);
            return new ListGamesResponse(gameArray);
        }
        catch (DataAccessException e) {
            throw new ErrorException(500,e.getMessage());
        }

    }
}
