package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private static HashMap<String, UserData> users = new HashMap<>();
    @Override
    public String createUser(UserData user) throws DataAccessException {
        String username = user.username();
        if (users.containsKey(username)) {
            throw new DataAccessException("username taken");
        }
        users.put(username, user);
        return username;
    }
    @Override
    public UserData getUser(String username) {
        UserData user = users.get(username);
        return user;
    }
    @Override
    public void clear() {
        users.clear();
    }
}
