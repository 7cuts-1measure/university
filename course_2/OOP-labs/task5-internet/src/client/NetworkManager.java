package client;

import java.io.IOException;

import common.protocol.Datagram;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;
import common.response.Response;

public class NetworkManager {
    private final Protocol protocol;

    public NetworkManager(Protocol protocol) {
        this.protocol = protocol;
    }

    public Response requsetAndGetResponse(Datagram datagram) {
        try {
            protocol.sendDatagram(datagram);
            Datagram d = protocol.receiveDatagram();
            return d instanceof Response ? (Response) d : null;
        } catch (IOException | UnsupportedProtocolException e) {
            e.printStackTrace();
        }
        return null;
    }
}
