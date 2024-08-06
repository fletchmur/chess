package clientdata;

import chess.ChessBoard;
import chess.ChessGame;

public class ClientData {

    private ChessGame board;
    private String user;

    public ClientData(ChessGame chessGame, String user) {
        this.board = chessGame;
        this.user = user;
    }
    public ChessGame getBoard() {
        return board;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void updateChessBoard(ChessBoard board) {
        this.board.setBoard(board);
    }
}
