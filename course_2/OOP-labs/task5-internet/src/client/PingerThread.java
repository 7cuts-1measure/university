package client;

import java.time.Duration;

import common.protocol.ConnectionLostException;
import common.request.PingRequest;
import common.response.ErrorResponse;
import common.response.PingResponse;
import common.response.Response;

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
                Response response = networkManager.doRequsetAndWaitResponse(pingRequest);
                if (response instanceof PingResponse) {
                    // ok
                } else if (response instanceof ErrorResponse) {
                    var er = (ErrorResponse) response;
                    System.err.println("Bad ping: " + er.reason);
                } else {
                    System.err.println("Unknown response: " + response);
                }
                Thread.sleep(Duration.ofSeconds(2));
            } catch (InterruptedException | ConnectionLostException e) {
                break;
            }
        }
    }

}
