package client;

import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import common.event.ChatMessageEvent;
import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.Datagram;
import common.protocol.ObjectProtocol;
import common.protocol.Protocol;

public class ClientApp {
    
    private final static Log log = new Log(LogLevel.DEBUG);
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket("localhost", 6969);

        log.debug("Connected");

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Protocol protocol = new ObjectProtocol(in, out);
        Datagram datagram = new ChatMessageEvent("text", "from");
        protocol.sendDatagram(datagram);
        
        System.in.read();
        protocol.close();
        socket.close();
    }
}
