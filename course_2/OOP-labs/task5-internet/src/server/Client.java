package server;

import java.io.IOException;

import common.event.Event;
import common.protocol.Protocol;

public class Client {
       
    public Client(String name, Protocol protocol, String type) {
        this.type = type;
        this.name = name;
        this.protocol = protocol;
    }

    private final String type;

    public String getType() {
        return type;
    }

    private final String name; 

    private final Protocol protocol;

    public String getName() {
        return name;
    }

    public void sendEvent(Event event) throws IOException {
        protocol.sendDatagram(event);
    }
}
