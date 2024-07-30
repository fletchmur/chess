package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import exception.ErrorException;
import request.*;
import response.*;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public ClearResponse clear(ClearRequest request) throws ErrorException {
        String path = "/db";
        String method = "DELETE";
        return makeRequest(method,path,request,null,ClearResponse.class);
    }

    public RegisterResponse register(RegisterRequest request) throws ErrorException {
        String path = "/user";
        String method = "POST";
        return makeRequest(method,path,request,null,RegisterResponse.class);
    }

    public LoginResponse login(LoginRequest request) throws ErrorException {
        String path = "/session";
        String method = "POST";
        return makeRequest(method,path,request,null,LoginResponse.class);
    }

    public CreateGameResponse createGame(CreateGameRequest request,String authToken) throws ErrorException {
        String path = "/game";
        String method = "POST";
        return makeRequest(method,path,request,authToken,CreateGameResponse.class);
    }

    public LogoutResponse logout(LogoutRequest request,String authToken) throws ErrorException {
        String path = "/session";
        String method = "DELETE";
        return makeRequest(method,path,request,authToken,LogoutResponse.class);
    }

    public ListGamesResponse listGames(ListGamesRequest request, String authToken) throws ErrorException {
        String path = "/game";
        String method = "GET";
        return makeRequest(method,path,null,authToken,ListGamesResponse.class);
    }

    public JoinGameResponse joinGame(JoinGameRequest request, String authToken) throws ErrorException {
        String path = "/game";
        String method = "PUT";
        return makeRequest(method,path,request,authToken,JoinGameResponse.class);
    }


     private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseType) throws ErrorException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }
            http.setDoOutput(true);

            //writes the request body and authToken into the http request
            writeBody(request,http);
            //sends the request to the server
            http.connect();
            throwIfNotSuccessful(http);
            //reads the response body from the http response
            return readBody(http,responseType);
        }
        catch (ErrorException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ErrorException(500,e.getMessage());
        }

    }


    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if(request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String requestJson = new Gson().toJson(request);
            try(OutputStream os = http.getOutputStream()) {
                os.write(requestJson.getBytes());
            }
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseType) throws IOException {
        T response = null;
        if(http.getContentLength() < 0) {
            try (InputStream responseBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if(responseType != null) {
                    response = new Gson().fromJson(reader, responseType);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ErrorException {
        int status = http.getResponseCode();
        boolean successful = status / 100 == 2;
        if(!successful) {
            throw new ErrorException(status,"failure " + status);
        }
    }
}
