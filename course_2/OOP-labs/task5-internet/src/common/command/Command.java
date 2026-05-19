package common.message;

import common.protocol.Datagram;

public interface Command extends Datagram{
    String getSessionId();
}
