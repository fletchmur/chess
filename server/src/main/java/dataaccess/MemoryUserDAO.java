package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final static HashMap<String, UserData> users = new HashMap<>();
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
    public void updateUser(String username, UserData newData) throws DataAccessException {
        UserData user = getUser(username);
        users.put(username, newData);
    }
    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("trying to remove nonexistent user");
        }
        users.remove(username);
    }
    @Override
    public void clear() {
        users.clear();
    }
}
