package common.command;

public class ListUsersRequest implements Request {

    private final String sessionId;

    public ListUsersRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    
}
