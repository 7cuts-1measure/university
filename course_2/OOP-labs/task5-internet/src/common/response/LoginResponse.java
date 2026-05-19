package common.response;


public class LoginResponse implements Response {
    private final String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public LoginResponse(String sessionId) {
        this.sessionId = sessionId;
    }
}
