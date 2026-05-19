package common.protocol;

import java.io.IOException;


public interface Protocol extends java.io.Closeable {
    
    void sendDatagram(Datagram msg) throws IOException;
    
    Datagram receiveDatagram() throws UnsupportedProtocolException, IOException;
}
