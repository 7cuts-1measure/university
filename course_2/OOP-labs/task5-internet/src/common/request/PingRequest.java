package common.request;

public class PingRequest implements Request{

    private final String sessionId;

    public PingRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }   
    
}
