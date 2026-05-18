package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.logging.Log;
import common.logging.LogLevel;

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
