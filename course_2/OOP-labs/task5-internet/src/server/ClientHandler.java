package server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

import common.event.Event;
import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.ConnectionLostException;
import common.protocol.Datagram;
import common.protocol.ObjectProtocol;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;
import common.request.ListUsersRequest;
import common.request.LoginRequest;
import common.request.LogoutRequest;
import common.request.MessageRequest;
import common.response.ErrorResponse;
import common.response.ListUsersResponse;
import common.response.LoginResponse;
import common.response.LogoutResponse;
import common.response.MessageResponse;

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

    private void processDatagram(Datagram datagram) throws ConnectionLostException {
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

    private void processChatMessage(MessageRequest msg) throws ConnectionLostException {
        String sessionId = msg.getSessionId();
        String text = msg.getText();

        Status status = chatRoom.checkMessage(sessionId);
        
        if (status.ok) {
            /*
             * Firstly we should send success response to client
             * and only then we can add (and broadcast) out message
             * becuase client expecting this behaviour
            */
            MessageResponse mr = new MessageResponse();
            protocol.sendDatagram(mr);
            chatRoom.addMessage(sessionId, text);
        } else {
            ErrorResponse er = new ErrorResponse(status.errorMessage);
            protocol.sendDatagram(er);
        }
    }

    private void processListUsersMessage(ListUsersRequest msg) throws ConnectionLostException {
        List<String> users = chatRoom.getUsersList();
        
        if (users == null) {
            protocol.sendDatagram(new ErrorResponse("Unexpected exception"));
        } else {
            protocol.sendDatagram(new ListUsersResponse(users));
        }
    }

    private void processLogoutMessage(LogoutRequest msg) throws ConnectionLostException {
        Status status = chatRoom.removeClient(msg.getSessionId());
        if (status.ok) {
            protocol.sendDatagram(new LogoutResponse());
        } else {
            protocol.sendDatagram(new ErrorResponse(status.errorMessage));
        }
    }

    private void processLoginMessage(LoginRequest msg) throws ConnectionLostException {
        
        String sessionId = UUID.randomUUID().toString();
        Status status = chatRoom.addClient(sessionId, new Client(msg.getUserName(), protocol, msg.getClientName()));
        
        if (status.ok) {
            LoginResponse response = new LoginResponse(sessionId);
            protocol.sendDatagram(response);
        } else {
            protocol.sendDatagram(new ErrorResponse(status.errorMessage));
        }
    }

    @Override
    public void run() {
        try {
            Datagram datagram;
            try {
                while ((datagram = protocol.receiveDatagram()) != null) {
                    log.debug("Got datagram: " + datagram);
                    processDatagram(datagram);
                }
            } catch (ConnectionLostException e) {
                log.err("Error happend when receiveng a datagram from cleunt");
            }

            protocol.close();
        } catch (UnsupportedProtocolException e) {
            log.err("Client uses unsupported protocol. Closing connection");
        }  catch (IOException e) {
            log.err("Got an error on client socket on port " + socket.getPort() + ": " + e.getMessage());
        } finally {
            log.info("Connection ended");      
        }
    } 
}
