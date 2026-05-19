package common.command;

import common.protocol.Datagram;

public interface Request extends Datagram{
    String getSessionId();
}
