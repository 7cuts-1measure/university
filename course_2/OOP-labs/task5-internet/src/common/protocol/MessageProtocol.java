package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import common.message.Message;

public interface MessageProtocol {
    void sendMessage(OutputStream out, Message msg) throws IOException;
    Message receiveMessage(InputStream in) throws UnsupportedProtocolException, IOException;
}
