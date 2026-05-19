package common.command;

import common.protocol.Datagram;

public interface Command extends Datagram{
    String getSessionId();
}
