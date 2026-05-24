package common.response;


public class LoginResponse implements Response {
    public final String sessionId;

    public LoginResponse(String sessionId) {
        this.sessionId = sessionId;
    }
}
