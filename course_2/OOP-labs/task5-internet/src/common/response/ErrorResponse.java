package common.response;


public class ErrorResponse implements Response {
    private final String reason;

    public String getReason() {
        return reason;
    }

    public ErrorResponse(String reason) {
        this.reason = reason;
    }
    
}
