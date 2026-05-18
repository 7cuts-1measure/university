package server;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

class ClientHandler implements Runnable {
    private final Socket socket;
    private final Log log = new Log(LogLevel.INFO);

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
            log.info("Connection ended");      
        } catch (IOException e) {
            log.err("Got an error when trying to listen on port "
                    + socket.getPort() + " or listening for a connection");
            log.err(e.getMessage());
        }
    } 
}

public class ServerApp {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Required port number");
            System.exit(1);
        }
        int portNumber = -1;
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            out.println("Wrong format for port number: " + args[0]);
            return;
        }

        Server server = new Server(portNumber);
        server.start();
    }
}