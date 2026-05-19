package common.message;


public class LogoutCommand implements Command {
    private final String sessionId;

    public LogoutCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    
}
