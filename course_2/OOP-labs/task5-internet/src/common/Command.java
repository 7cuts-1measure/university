package common;

public class Command {
    
    private static final int UNDEFINED_SESSION = -1;
    
    private final int sessionId;
    
    public Command(int sessionId) {
        this.sessionId = sessionId;
    }
    public int getSessionId() {
        return sessionId;
    }
}
