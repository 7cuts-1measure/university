package client;

import common.event.Event;

public interface EventListener {
    void onEvent(Event event);

    void onConnectionLost();
}
