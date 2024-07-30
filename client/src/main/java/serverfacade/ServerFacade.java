package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import exception.ErrorException;
import request.*;
import response.*;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public ClearResponse clear(ClearRequest request) throws ErrorException, IOException {
        String path = serverURL + "/db";
        String method = "DELETE";
        try {
            URL url = new URL(path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            //write to the request body
            if(request != null) {
                http.setRequestProperty("Content-Type", "application/json");
                try(OutputStream os = http.getOutputStream()) {
                    String requestJson = new Gson().toJson(request);
                    os.write(requestJson.getBytes());
                }
            }

            http.connect();

            //check to see if the request is successful, if not throw an error
            if(http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ErrorException(http.getResponseCode(), "failure: " + http.getResponseMessage());
            }

            ClearResponse response = null;
            if (http.getContentLength() < 0) {
                try(InputStream is = http.getInputStream()) {
                    response = new Gson().fromJson(new InputStreamReader(is), ClearResponse.class);
                }
            }

            return response;
        }
        catch (Exception e) {
            throw new ErrorException(500,e.getMessage());
        }
    }

    /*
     private <T> T makeRequest(String method, String path, Object request, Class<T> responseType) throws ErrorException {
        try {
            URL url = new URL(serverURL + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request,http);
            http.connect();
            throwIfNotSuccessful(http);
            return new T;
        }
        catch (Exception e) {
            throw new ErrorException(500,e.getMessage());
        }

    }
     */


    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if(request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String requestJson = new Gson().toJson(request);
            try(OutputStream os = http.getOutputStream()) {
                os.write(requestJson.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ErrorException {
        int status = http.getResponseCode();
        boolean successful = status / 100 == 2;
        if(!successful) {
            throw new ErrorException(status,"failure " + status);
        }

    }

}
