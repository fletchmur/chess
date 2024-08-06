package websocket.messages;

import java.util.Objects;

public class Notification extends ServerMessage {

    private final String msg;

    public Notification(String msg) {
        super(ServerMessageType.NOTIFICATION);
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Notification that = (Notification) o;
        return Objects.equals(msg, that.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), msg);
    }
}
