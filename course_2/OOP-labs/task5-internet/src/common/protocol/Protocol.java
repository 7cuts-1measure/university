package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface Protocol {
    
    void sendDatagram(OutputStream out, Datagram msg) throws IOException;
    
    Datagram receiveDatagram(InputStream in) throws UnsupportedProtocolException, IOException;
}
