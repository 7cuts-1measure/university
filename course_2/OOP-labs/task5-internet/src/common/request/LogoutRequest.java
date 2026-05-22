package common.request;


public class LogoutRequest implements Request {
    private final String sessionId;

    public LogoutRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    
}
