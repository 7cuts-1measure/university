package common.response;


public class ErrorResponse implements Response {
    public final String reason;

    public ErrorResponse(String reason) {
        this.reason = reason;
    }
    
}
