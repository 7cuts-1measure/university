package common.protocol;

public interface Protocol extends java.io.Closeable {
    
    void sendDatagram(Datagram msg) throws ConnectionLostException;
    
    Datagram receiveDatagram() throws UnsupportedProtocolException, ConnectionLostException;
}
