package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    private static HashSet<AuthData> authDataSet;
    @Override
    public void createAuth(AuthData data)
    {
        authDataSet.add(data);
    }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        for (AuthData authData : authDataSet) {
            if(authData.authToken().equals(authToken))
            {
                return authData;
            }
        }

        throw new DataAccessException("Auth token not found");
    }
    @Override
    public void updateAuth(String authToken, AuthData data) throws DataAccessException
    {
        deleteAuth(authToken);
        createAuth(data);
    }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException
    {
        for (AuthData authData : authDataSet) {
            if(authData.authToken().equals(authToken))
            {
                authDataSet.remove(authData);
                return;
            }
        }
        throw new DataAccessException("Auth token not found when deleting");
    }
}
