package server;

import common.event.ChatMessageEvent;

public class Message {
    
    private final String from;

    private final String text;

    public String getFrom() {
        return from;
    }

    public String getText() {
        return text;
    }

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
    }

    public ChatMessageEvent toChatMessageEvent() {
        return new ChatMessageEvent(text, from);
    }

}
