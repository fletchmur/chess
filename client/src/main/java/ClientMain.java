import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        ChessClient chessClient = new ChessClient("http://localhost:8080");
        chessClient.run();
    }
}