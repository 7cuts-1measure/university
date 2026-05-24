package server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import common.logging.Log;
import common.logging.LogLevel;

/**
 * Thread safety chat room. (Because multiple {@code ClientHadnler}s have access to it)
 */
public class ChatRoom {
    
    private final Log log = new Log(LogLevel.INFO);

    private final Map<String, Client> clients = new HashMap<>();

    private final List<Message> history = new LinkedList<>();

    private final static int HISTORY_SIZE = 100;

    public synchronized void addClient(String sessionId, Client client) {   
        log.info("Add client: [sessionId=" + sessionId + ", name=" + client.getName() + ", type=" + client.getType() + "]"); 
        clients.put(sessionId, client);
        sendHistory(client);
    }

    public synchronized void addMessage(String sessionId, String text) {
        Client client = clients.get(sessionId);
        if (client == null) {
            log.warn("Got message with bad sessionId. Ignoring");
            return;
        }

        Message msg = new Message(client.getName(), text);

        log.info("Got message: " + msg);

        if (history.size() == HISTORY_SIZE) {
            history.removeLast();
            history.addFirst(msg);
        }
    }


    private void sendHistory(Client client) {
        List<Message> historyCopy;
        synchronized(this) {
            // history can change while we sending it to client
            // but we don't want to take a lock on whole function because
            // sending something could be slow
            historyCopy = List.copyOf(history);
        }

        for (var message : historyCopy) {
            try {
                client.sendEvent(message.toChatMessageEvent());
            } catch (IOException e) {
                // TODO: may be resend it to client after some time?
                log.err("Cannot send message to client " + client.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public void removeClient(String sessionId) {
        Client client = clients.remove(sessionId);
        if (client == null) {
            log.warn("Cannot remove client from chat room: There are no such sessionId: " + sessionId);
        }

        log.info("Remove client: [sessionId=" + sessionId + ", name=" + client.getName() + ", type=" + client.getType() + "]"); 
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
 