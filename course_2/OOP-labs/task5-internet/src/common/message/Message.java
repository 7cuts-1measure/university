package common.message;

import common.protocol.Datagram;

public interface Message extends Datagram{
    String getSessionId();
}
