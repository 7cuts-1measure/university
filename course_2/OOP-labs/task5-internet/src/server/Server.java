package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.Log;
import common.LogLevel;

class Server {
    private final int portNumber;
    private final Log log = new Log(LogLevel.INFO);

    private ServerSocket serverSocket = null;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Cleaning resources...");
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        /* Allow threads to finish their tasks */
        threadPool.shutdown();
    }

    public Server(int portNumber) {
        this.portNumber = portNumber;
        registerShutDownHook();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(portNumber);
            Socket client = serverSocket.accept();
            threadPool.submit(new ClientHandler(client));
        } catch (IOException e) {
            log.err(e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();

                } catch (IOException e) {
                    log.err("Cannot close server socket");
                    e.printStackTrace();
                }
            }
        }
    }
}
