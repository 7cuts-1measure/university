package common.message;

public class ChatMessage implements Message {

    public ChatMessage(String sessionId, String text) {
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
