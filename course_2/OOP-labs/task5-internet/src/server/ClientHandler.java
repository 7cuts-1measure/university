package server;

import java.io.IOException;
import java.net.Socket;

import common.event.Event;
import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.Datagram;
import common.protocol.ObjectProtocol;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;
import common.request.ListUsersRequest;
import common.request.LoginRequest;
import common.request.LogoutRequest;
import common.request.MessageRequest;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final Log log = new Log(LogLevel.DEBUG);

    private final ChatRoom chatRoom;

    private final Protocol protocol;

    ClientHandler(Socket socket, ChatRoom chatRoom) throws IOException {
        this.socket = socket;
        this.chatRoom = chatRoom;
        protocol = new ObjectProtocol(socket.getInputStream(), socket.getOutputStream());
    }

    private void processDatagram(Datagram datagram) {
        if (datagram instanceof Event) {
            log.warn("Got an event from client. Only server is allowed to send events => ignore");
        } else if (datagram instanceof LoginRequest) {
            processLoginMessage((LoginRequest) datagram);
        } else if (datagram instanceof LogoutRequest) {
            processLogoutMessage((LogoutRequest) datagram);
        } else if (datagram instanceof ListUsersRequest) {
            processListUsersMessage((ListUsersRequest) datagram);
        } else if (datagram instanceof MessageRequest) {
            processChatMessage((MessageRequest) datagram);
        }
    }

    private void processChatMessage(MessageRequest msg) {
        chatRoom.addMessage(msg.getSessionId(), msg.getText());
    }

    private void processListUsersMessage(ListUsersRequest msg) {
        chatRoom.getUsersList();
        // TODO: protocol.sendDatagram(kind of response);
    }

    private void processLogoutMessage(LogoutRequest msg) {
        chatRoom.removeClient(msg.getSessionId());
    }

    private void processLoginMessage(LoginRequest msg) {
        chatRoom.addClient(msg.getSessionId(), new Client(msg.getUserName(), protocol, msg.getClientName()));
    }

    @Override
    public void run() {
        try {
            Datagram datagram;
            while ((datagram = protocol.receiveDatagram()) != null) {
                log.debug("Got datagram: " + datagram);
                processDatagram(datagram);
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
