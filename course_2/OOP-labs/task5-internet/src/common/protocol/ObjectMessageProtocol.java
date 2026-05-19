package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import common.message.Message;

public class ObjectMessageProtocol implements MessageProtocol{

    @Override
    public void sendMessage(OutputStream out, Message msg) throws IOException {
        var oos = new ObjectOutputStream(out);
        oos.writeObject(msg);
    }

    @Override
    public Message receiveMessage(InputStream in) throws UnsupportedProtocolException, IOException {
        try (var ois = new ObjectInputStream(in)) {
            Message msg = (Message) ois.readObject();
            return msg;
        } catch (ClassNotFoundException e) {
            throw new UnsupportedProtocolException();
        }
    }
}
