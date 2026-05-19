package common.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import common.message.Message;

public class XMLMessageProtocol implements MessageProtocol{

    @Override
    public void sendMessage(OutputStream out, Message msg) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }

    @Override
    public Message receiveMessage(InputStream in) throws UnsupportedProtocolException, IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveMessage'");
    }
    
}
