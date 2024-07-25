package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.MySQLDAO;
import dataaccess.interfaces.UserDAO;
import model.UserData;
import service.ErrorException;

public class MySQLUserDAO extends MySQLDAO implements UserDAO  {

    public MySQLUserDAO() throws ErrorException {
        //create the user table if it doesn't exist
        super.configureDatabase();
    }

    @Override
    protected String[] instantiateCreateStatements() {
        //TODO write create statements to make the table
        return new String[0];
    }

    @Override
    public String createUser(UserData user) throws DataAccessException {
        //TODO write method to add a user to the database
        return "";
    }

    @Override
    public UserData getUser(String username) {
        //TODO write method to get a user from the database
        return null;
    }

    @Override
    public void clear() {
        //TODO write method to clear everything from the user table in the database
    }
}
