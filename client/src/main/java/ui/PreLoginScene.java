package ui;

import client.ClientData;
import exception.ErrorException;
import request.LoginRequest;
import request.RegisterRequest;
import facades.ServerFacade;

import java.util.HashMap;

public class PreLoginScene extends Scene {

    private final ServerFacade facade;
    private final SceneManager sceneManager;
    private final HashMap<String, UIFunction<String[],String>> validCommands;
    private ClientData data;

    public PreLoginScene(SceneManager sceneManager, ServerFacade facade, ClientData data) {
        this.sceneManager = sceneManager;
        this.facade = facade;
        this.data = data;
        validCommands = new HashMap<>();
        validCommands.put("register",this::register);
        validCommands.put("login",this::login);
        validCommands.put("quit",this::quit);
    }

    @Override
    public String help() {
        String prelude = BOLD_FORMAT + "[HELP]\n";
                String helpString =  BOLD_FORMAT + "register <USERNAME> <PASSWORD> <EMAIL>" + SERVER_FORMAT + " - to create an account\n" +
                BOLD_FORMAT + "login <USERNAME <PASSWORD>" + SERVER_FORMAT + " - login to an existing account\n" +
                BOLD_FORMAT + "quit" + SERVER_FORMAT + " - exit program\n" +
                BOLD_FORMAT + "help" + SERVER_FORMAT + " - show possible commands";
        return prelude + helpString;
    }

    @Override
    HashMap<String, UIFunction<String[],String>> getValidMethods() {
        return validCommands;
    }

    private String login(String... params) throws ErrorException {

        if(params.length != 2) {
            return SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " login <USERNAME> <PASSWORD>";
        }

        String username = params[0];
        String password = params[1];

        LoginRequest loginRequest = new LoginRequest(username, password);
        facade.login(loginRequest);
        sceneManager.setState(SceneManager.State.SIGNED_IN);
        data.setUser(username);

        return SERVER_FORMAT + "Successfully Logged In";
    }

    private String register(String... params) throws ErrorException {

        if(params.length != 3) {
            return  SERVER_FORMAT + "Expected Usage:" + BOLD_FORMAT + " register <USERNAME> <PASSWORD> <EMAIL>";
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        facade.register(new RegisterRequest(username, password, email));
        sceneManager.setState(SceneManager.State.SIGNED_IN);
        data.setUser(username);

        return SERVER_FORMAT + "Registered user: " + username;
    }

    private String quit(String... params) throws ErrorException {
        SceneManager.State state = sceneManager.getState();
        return switch (state) {
            case SIGNED_OUT ->  "quit";
            case SIGNED_IN,GAMEPLAY -> throw new ErrorException(400, "Must logout before quitting program");
        };
    }
}
