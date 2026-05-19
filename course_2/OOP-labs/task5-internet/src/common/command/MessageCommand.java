package common.message;

public class MessageCommand implements Command {

    public MessageCommand(String sessionId, String text) {
        this.sessionId = sessionId;
        this.text = text;
    }

    private final String sessionId;

    private final String text;
    

    @Override
    public String getSessionId() {
        return sessionId;
    }


    public String getText() {
        return text;
    }
    
}
