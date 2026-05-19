package common.message;

public class ListUsersCommand implements Command {

    private final String sessionId;

    public ListUsersCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    
}
