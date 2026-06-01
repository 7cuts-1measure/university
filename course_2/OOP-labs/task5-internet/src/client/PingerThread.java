package client;

import java.time.Duration;

import common.protocol.ConnectionLostException;
import common.request.PingRequest;

public class PingerThread  extends Thread{

    private final String sessionId;
    private NetworkManager networkManager;

    public String getSessionId() {
        return sessionId;
    }

    public final NetworkManager getNetworkManager() {
        return networkManager;
    }

    public PingerThread(String sessionId, NetworkManager networkManager) {
        this.sessionId = sessionId;
        this.networkManager = networkManager;
    }


    @Override
    public void run() {
        while (!Thread.interrupted()) {
            PingRequest pingRequest = new PingRequest(sessionId);
            try {
                networkManager.doRequsetAndWaitResponse(pingRequest);
                Thread.sleep(Duration.ofSeconds(2));
            } catch (InterruptedException | ConnectionLostException e) {
                break;
            }
        }
    }

}
