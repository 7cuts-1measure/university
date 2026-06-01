package common.event;

public class ChatMessageEvent implements Event{

    private final String text;

    private final String from;

    public String getText() {
        return text;
    }

    public String getFrom() {
        return from;
    }

    public ChatMessageEvent(String text, String from) {
        this.text = text;
        this.from = from;
    }

    @Override
    public String getUserName() {
        return from;
    }

}
