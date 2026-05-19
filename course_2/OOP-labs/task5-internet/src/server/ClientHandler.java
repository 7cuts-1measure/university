package server;

import java.io.IOException;
import java.net.Socket;

import common.command.ListUsersCommand;
import common.command.LoginCommand;
import common.command.LogoutCommand;
import common.command.MessageCommand;
import common.event.ChatMessageEvent;
import common.event.Event;
import common.event.UserConnectedEvent;
import common.event.UserDisconnectedEvent;
import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.Datagram;
import common.protocol.ObjectProtocol;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;

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
        } else if (datagram instanceof LoginCommand) {
            processLoginMessage((LoginCommand) datagram);
        } else if (datagram instanceof LogoutCommand) {
            processLogoutMessage((LogoutCommand) datagram);
        } else if (datagram instanceof ListUsersCommand) {
            processListUsersMessage((ListUsersCommand) datagram);
        } else if (datagram instanceof MessageCommand) {
            processChatMessage((MessageCommand) datagram);
        }
    }

    private void processChatMessage(MessageCommand msg) {
        chatRoom.addMessage(msg.getSessionId(), msg.getText());
    }

    private void processListUsersMessage(ListUsersCommand msg) {
        chatRoom.getUsersList();
        // TODO: protocol.sendDatagram(kind of response);
    }

    private void processLogoutMessage(LogoutCommand msg) {
        chatRoom.removeClient(msg.getSessionId());
    }

    private void processLoginMessage(LoginCommand msg) {
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
