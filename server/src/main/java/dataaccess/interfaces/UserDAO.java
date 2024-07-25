package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {
    public String createUser(UserData user) throws DataAccessException;
    public UserData getUser(String username) throws DataAccessException;
    public void clear() throws DataAccessException;
}
