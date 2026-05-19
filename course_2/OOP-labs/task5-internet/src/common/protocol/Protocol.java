package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface Protocol {
    
    void sendMessage(OutputStream out, Datagram msg) throws IOException;
    
    Datagram receiveMessage(InputStream in) throws UnsupportedProtocolException, IOException;
}
