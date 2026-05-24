package client;

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

    public void startEventListener(EventListener eventListener) {
        listenerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Datagram datagram = protocol.receiveDatagram();
                    System.err.println("Get datagram: " + datagram);
                    if (datagram instanceof Event) {
                        eventListener.onEvent((Event) datagram);
                    } else if (datagram instanceof Response) {
                        updateLastResponse((Response) datagram);
                    } else {
                        System.err.println("Got invalid datagram: " + datagram);
                    }
                } catch (UnsupportedProtocolException | ConnectionLostException e) {
                    // Не должно случиться, т.к. протокол уже установлен
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted");
                    break;
                }
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
    public Response waitResponse() throws InterruptedException {
        synchronized (lastResponseLock) {
            while (lastResponse == null) {
                lastResponseLock.wait();
            }
            Response res = lastResponse;
            lastResponse = null;
            lastResponseLock.notifyAll();
            return res;
        }
    }

    // TODO: may be retry sending data after some time if got some error and then destroy...
    public Response doRequsetAndWaitResponse(Datagram datagram) throws ConnectionLostException, InterruptedException {
        protocol.sendDatagram(datagram);
        return waitResponse();
    }
}
