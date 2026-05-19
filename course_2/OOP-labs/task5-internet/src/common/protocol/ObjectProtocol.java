package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectProtocol implements Protocol{

    @Override
    public void sendDatagram(OutputStream out, Datagram msg) throws IOException {
        var oos = new ObjectOutputStream(out);
        oos.writeObject(msg);
    }

    @Override
    public Datagram receiveDatagram(InputStream in) throws UnsupportedProtocolException, IOException {
        try (var ois = new ObjectInputStream(in)) {
            Datagram msg = (Datagram) ois.readObject();
            return msg;
        } catch (ClassNotFoundException e) {
            throw new UnsupportedProtocolException();
        }
    }
}
