package common.event;

import common.protocol.Datagram;

public interface Event extends Datagram{
    public String getUserName();   
}
