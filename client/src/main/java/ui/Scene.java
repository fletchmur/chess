package ui;

import exception.ErrorException;

import java.util.HashMap;

public abstract class Scene {

    static final String BOLD_FORMAT = EscapeSequences.BOLD_SERVER_FORMAT;
    static final String SERVER_FORMAT = EscapeSequences.FAINT_SERVER_FORMAT;

    @FunctionalInterface
    protected interface UIFunction<T,R> {
        R apply(T t) throws ErrorException;
    }

    public String eval(String command,String... params) throws ErrorException {
        try {
            HashMap<String, UIFunction<String[],String>> uiMethods = getValidMethods();
            UIFunction<String[],String> method = uiMethods.get(command);

            if (method == null) {
                return help();
            }
            return method.apply(params);
        }
        catch (ErrorException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ErrorException(500,e.getMessage());
        }

    }
    abstract String help();
    abstract HashMap<String, UIFunction<String[],String>> getValidMethods();

}
