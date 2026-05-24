package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.ConnectionLostException;

/**
 * Thread safety chat room. (Because multiple {@code ClientHadnler}s have access
 * to it)
 */
public class ChatRoom {

    private final Log log = new Log(LogLevel.INFO);

    private final Map<String, Client> clients = new HashMap<>();

    private final List<Message> history = new LinkedList<>();

    private final static int HISTORY_SIZE = 100;

    public synchronized Status addClient(String sessionId, Client client) {
        log.info("Add client: [sessionId=" + sessionId + ", name=" + client.getName() + ", type=" + client.getType()
                + "]");

        if (clients.containsKey(sessionId)) {
            String msg = "Other client on the server has the same sessionID";
            log.warn(msg);
            return new Status(false, msg);
        }

        clients.put(sessionId, client);
        sendHistory(client);

        return new Status(true, null);
    }

    public synchronized Status checkMessage(String sessionId) {
        Client client = clients.get(sessionId);
        if (client == null) {
            String error = "There are no such sessionId on the server: " + sessionId;
            log.warn(error);
            return new Status(false, error);
        }
        return new Status(true, null);
    }

    public synchronized void addMessage(String sessionId, String text) {
        Client client = clients.get(sessionId);
        Status status = checkMessage(sessionId);

        if (!status.ok) {
            return;
        }

        Message msg = new Message(client.getName(), text);

        log.info("Got message: " + msg);

        if (history.size() == HISTORY_SIZE) {
            history.removeLast();
            history.addFirst(msg);
        }

        broadcast(msg);
    }

    private void broadcast(Message msg) {
        for (var pair : clients.entrySet()) {
            Client client = pair.getValue();
            String sessionId = pair.getKey();
            try {
                client.sendEvent(msg.toChatMessageEvent());
            } catch (ConnectionLostException e) {
                log.err("Failed to send message to " + client.getName() + ", SessionID=" + sessionId);
            }
        }
    }

    private synchronized Status sendHistory(Client client) {
        List<Message> historyCopy;
            // history can change while we sending it to client
            // but we don't want to take a lock on whole function because
            // sending something could be slow
            historyCopy = List.copyOf(history);

        for (var message : historyCopy) {
            try {
                client.sendEvent(message.toChatMessageEvent());
            } catch (ConnectionLostException e) {
                // TODO: may be resend it to client after some time?
                String errorMsg = "Cannot send message to client " + client.getName() + ": " + e.getLocalizedMessage();
                log.err(errorMsg);
                return new Status(false, errorMsg);
            }
        }

        return new Status(true, null);
    }

    public Status removeClient(String sessionId) {
        Client client = clients.remove(sessionId);
        if (client == null) {
            String errorMsg = "Cannot remove client from chat room: There are no such sessionId: " + sessionId;
            log.warn(errorMsg);
            return new Status(true, errorMsg);
        }

        log.info("Remove client: [sessionId=" + sessionId + ", name=" + client.getName() + ", type=" + client.getType()
                + "]");
        return new Status(true, null);
    }

    public synchronized List<String> getUsersList() {
        // TODO: optimize this function
        List<String> users = new ArrayList<>(clients.size());
        for (var client : clients.values()) {
            users.add(client.getName());
        }
        return users;
    }

}
