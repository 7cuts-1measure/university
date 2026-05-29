package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.event.Event;
import common.event.UserConnectedEvent;
import common.event.UserDisconnectedEvent;
import common.logging.Log;
import common.logging.LogLevel;
import common.protocol.ConnectionLostException;

/**
 * Thread safety chat room. (Because multiple ClientHadnlers have access
 * to it)
 */
public class ChatRoom {

    private final Log log = new Log(LogLevel.INFO);

    private final Map<String, Client> clients = new HashMap<>();
    private final Set<String> uniqueNames = new HashSet<>();

    private final List<Message> history = new LinkedList<>();

    private final static int HISTORY_SIZE = 10;

    public synchronized Status addClient(String sessionId, Client client) {
        log.info("Add client: [sessionId=" + sessionId + ", name=" + client.getName() + ", type=" + client.getType()
                + "]");

        if (clients.containsKey(sessionId)) {
            String msg = "Other client on the server has the same sessionID";
            log.warn(msg);
            return new Status(false, msg);
        }
        if (uniqueNames.contains(client.getName())) {
            String msg = "Other client on the server has the same name";
            log.warn(msg);
            return new Status(false, msg);
        }
        uniqueNames.add(client.getName());
        
        // Order is important
        // 1. Broadcast
        broadcast(new UserConnectedEvent(client.getName()));
        // 2. Then add
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
            history.removeFirst();
        }
        history.addLast(msg);

        broadcast(msg.toChatMessageEvent());
    }

    private void broadcast(Event event) {
        for (var pair : clients.entrySet()) {
            Client client = pair.getValue();
            String sessionId = pair.getKey();
            try {
                client.sendEvent(event);
            } catch (ConnectionLostException e) {
                log.err("Failed to send message to " + client.getName() + ", SessionID=" + sessionId);
            }
        }
    }

    private synchronized Status sendHistory(Client client) {
        for (var message : history) {
            try {
                client.sendEvent(message.toChatMessageEvent());
            } catch (ConnectionLostException e) {
                String errorMsg = "Cannot send message to client " + client.getName() + ": " + e.getLocalizedMessage();
                log.err(errorMsg);
                return new Status(false, errorMsg);
            }
        }

        return new Status(true, null);
    }

    public Status removeClient(String sessionId, String reason) {
        Client client = clients.remove(sessionId);
        if (client == null) {
            String errorMsg = "Cannot remove client from chat room: There are no such sessionId: " + sessionId;
            log.warn(errorMsg);
            return new Status(true, errorMsg);
        }

        log.info("Remove client: [sessionId=" + sessionId + ", name=" + client.getName() + ", type=" + client.getType()
                + "]");

        uniqueNames.remove(client.getName());
        UserDisconnectedEvent ude = new UserDisconnectedEvent(client.getName(), reason);
        broadcast(ude);
        return new Status(true, null);
    }

    public synchronized List<String> getUsersList() {
        List<String> users = new ArrayList<>(clients.size());
        for (var client : clients.values()) {
            users.add(client.getName());
        }
        return users;
    }

    public Status ping(String sessionId) {
        Client client = clients.get(sessionId);
        if (client == null) {
            String errorMsg = "Cannot receive ping from client: There are no such sessionId: " + sessionId;
            log.warn(errorMsg);
            return new Status(false, errorMsg);
        }
        client.ping();
        return new Status(true, null);
    }

}
