package server;

public class Status {
    public final boolean ok;

    public final String errorMessage;

    public Status(boolean ok, String errorMessage) {
        this.ok = ok;
        this.errorMessage = errorMessage;
    }

}
