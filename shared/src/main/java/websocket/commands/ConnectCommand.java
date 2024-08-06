package websocket.commands;

import chess.ChessGame;

import java.util.Objects;

public class ConnectCommand extends UserGameCommand {
    private ChessGame.TeamColor color;

    public ConnectCommand(String rootClient, String authToken, Integer gameID, ChessGame.TeamColor color) {
        super(CommandType.CONNECT,rootClient,authToken,gameID);
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        return this.color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ConnectCommand that = (ConnectCommand) o;
        return getColor() == that.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getColor());
    }
}
