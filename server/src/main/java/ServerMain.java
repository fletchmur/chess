import chess.*;
import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        Server server = new Server();
        int port = 8080;
        server.run(port);
        System.out.println("♕ Chess Server listening on port: " + port);
    }
}