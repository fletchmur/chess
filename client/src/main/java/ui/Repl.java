package ui;

import java.util.Scanner;

public class Repl {

    private ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + "♕ Welcome to Chess. Enter help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(result != "quit") {
            printPropmt();
            String line = scanner.nextLine();
            result = client.eval(line);
            System.out.print(result);
            System.out.println();
        }
    }

    private void printPropmt() {
        System.out.println();
        System.out.print(client.getStateString() + EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + " >>> ");
    }
}
