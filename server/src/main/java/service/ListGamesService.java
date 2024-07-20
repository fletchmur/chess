package service;

import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import model.GameData;
import response.ListGamesResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListGamesService {

    GameDAO gameDAO = new MemoryGameDAO();

    public ListGamesResponse listGames() {
        Collection<GameData> games = gameDAO.getAllGames();
        List<GameData> gamesList = new ArrayList<>(games);
        GameData[] gameArray = gamesList.toArray(new GameData[gamesList.size()]);
        return new ListGamesResponse(gameArray);
    }
}
