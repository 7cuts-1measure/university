package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectProtocol implements Protocol{

    private final ObjectInputStream ois;

    private final ObjectOutputStream oos;

  
    public ObjectProtocol(InputStream in, OutputStream out) throws IOException {
        //  !!!! ORDER OF INITIALIZATION IS IMPORTANT !!!!!
        //  ObjectOutputStream must be created first to write the stream header,
        //  then ObjectInputStream can read the server's response header.
        //  Reversing the order (OIS first) would cause deadlock as both sides wait for each other's headers.
        oos = new ObjectOutputStream(out);
        ois = new ObjectInputStream(in);
    }

    @Override
    public void close() throws IOException {
        ois.close();
        oos.close();
    }


    @Override
    public void sendDatagram(Datagram msg) throws IOException {
        oos.writeObject(msg);
    }

    @Override
    public Datagram receiveDatagram() throws UnsupportedProtocolException, IOException {
        try {
            return (Datagram) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new UnsupportedProtocolException();
        }
    }
}
