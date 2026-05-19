package server;

import java.io.IOException;
import java.net.Socket;

import common.event.ChatMessageEvent;
import common.event.Event;
import common.event.UserConnectedEvent;
import common.event.UserDisconnectedEvent;
import common.logging.Log;
import common.logging.LogLevel;
import common.message.ChatMessage;
import common.message.ListUsersMessage;
import common.message.LoginMessage;
import common.message.LogoutMessage;
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

    private void processDatagram(Datagram datagram) {
        if (datagram instanceof Event) {
            log.warn("Got an event from client. Only server is allowed to send events => ignore");
        } else if (datagram instanceof LoginMessage) {
            processLoginMessage((LoginMessage) datagram);
        } else if (datagram instanceof LogoutMessage) {
            processLogoutMessage((LogoutMessage) datagram);
        } else if (datagram instanceof ListUsersMessage) {
            processListUsersMessage((ListUsersMessage) datagram);
        } else if (datagram instanceof ChatMessage) {
            processChatMessage((ChatMessage) datagram);
        }
    }

    private void processChatMessage(ChatMessage msg) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processChatMessage'");
    }

    private void processListUsersMessage(ListUsersMessage msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processListUsersMessage'");
    }

    private void processLogoutMessage(LogoutMessage msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processLogoutMessage'");
    }

    private void processLoginMessage(LoginMessage msg) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processLoginMessage'");
    }

    @Override
    public void run() {
    
        try {
            
            Protocol protocol = new ObjectProtocol(socket.getInputStream(), socket.getOutputStream());

            Datagram datagram;
            while ((datagram = protocol.receiveDatagram()) != null) {
                log.debug("Got datagram: " + datagram);
                processDatagram(datagram);
                // process datagram
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
