package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {
    public String createUser(UserData user) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    public void updateUser(String username, UserData newData) throws DataAccessException;
    public void deleteUser(String username) throws DataAccessException;
    public void clear();
}
