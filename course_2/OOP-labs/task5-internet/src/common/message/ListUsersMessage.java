package common.message;

public class ListUsersMessage implements Message {

    private final String sessionId;

    public ListUsersMessage(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    
}
