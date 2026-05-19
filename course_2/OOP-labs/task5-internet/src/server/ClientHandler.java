package server;

import java.io.IOException;
import java.net.Socket;

import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.Datagram;
import common.protocol.ObjectProtocol;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final Log log = new Log(LogLevel.DEBUG);

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
    
        try {
            
            Protocol protocol = new ObjectProtocol(socket.getInputStream(), socket.getOutputStream());

            Datagram datagram;
            while ((datagram = protocol.receiveDatagram()) != null) {
                // process datagram
                log.debug("Got datagram: " + datagram);
            }
            
            protocol.close();
        } catch (IOException e) {

            log.err("Got an error on client socket on port " + socket.getPort() + ": " + e.getMessage());
        
        } catch (UnsupportedProtocolException e) {
            log.err("Client uses unsupported protocol. Closing connection");
        
        } finally {
            log.info("Connection ended");      
        }
    } 
}
