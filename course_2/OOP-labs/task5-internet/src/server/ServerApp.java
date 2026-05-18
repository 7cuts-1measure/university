package server;

import static java.lang.System.out;

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