package websocket.messages;

import chess.ChessBoard;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {
    private final ChessBoard game;

    public LoadGameMessage(ChessBoard gameBoard) {
        super(ServerMessageType.LOAD_GAME);
        this.game = gameBoard;
    }

    public ChessBoard getGameBoard() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        LoadGameMessage that = (LoadGameMessage) o;
        return Objects.equals(getGameBoard(), that.getGameBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGameBoard());
    }
}
