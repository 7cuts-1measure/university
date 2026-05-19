package common.message;


public class LogoutMessage implements Message {
    private final String sessionId;

    public LogoutMessage(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    
}
