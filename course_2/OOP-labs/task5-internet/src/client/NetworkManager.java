package client;

import java.util.concurrent.atomic.AtomicBoolean;

import common.event.Event;
import common.protocol.ConnectionLostException;
import common.protocol.Datagram;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;
import common.response.Response;

public class NetworkManager {
    private final Protocol protocol;

    public NetworkManager(Protocol protocol) {
        this.protocol = protocol;
    }

    private Thread listenerThread = null;


    private Response lastResponse = null;
    private final Object lastResponseLock = new Object();

    private final AtomicBoolean isLostConnection = new AtomicBoolean(false);

    public void startEventListener(EventListener eventListener) {
        listenerThread = new Thread(() -> {
            try { 
                while (!Thread.interrupted()) {                
                    Datagram datagram = protocol.receiveDatagram();
                    System.err.println("Get datagram: " + datagram);
                    if (datagram instanceof Event) {
                        eventListener.onEvent((Event) datagram);
                    } else if (datagram instanceof Response) {
                        updateLastResponse((Response) datagram);
                    } else {
                        System.err.println("Got invalid datagram: " + datagram);
                    }
                } 
            } catch (InterruptedException | UnsupportedProtocolException | ConnectionLostException e) {
                System.err.println("ERROR: Connection lost");
                Thread.currentThread().interrupt();
                eventListener.onConnectionLost();
            }            
        });
        listenerThread.setDaemon(true); // чтобы JVM могла завершиться при закрытии окна
        listenerThread.start();
    }

    private void updateLastResponse(Response response) throws InterruptedException {
        synchronized (lastResponseLock) {
            while (lastResponse != null) {
                lastResponseLock.wait();
            }
            lastResponse = response;
            lastResponseLock.notifyAll();
        }
    }

    // TODO: add timeout
    public Response waitResponse() throws InterruptedException, ConnectionLostException {
        synchronized (lastResponseLock) {
            while (lastResponse == null) {
                lastResponseLock.wait();
            }
            // what if connection lost?
            if (isLostConnection.get() == true) {
                throw new ConnectionLostException();
            }

            Response res = lastResponse;
            lastResponse = null;
            lastResponseLock.notifyAll();
            return res;
        }
    }

    public Response doRequsetAndWaitResponse(Datagram datagram) throws ConnectionLostException, InterruptedException {
        protocol.sendDatagram(datagram);
        return waitResponse();
    }
}
