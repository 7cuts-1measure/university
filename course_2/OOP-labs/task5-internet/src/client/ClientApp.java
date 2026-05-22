package client;
import javax.swing.*;

public class ClientApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (!login.isSucceeded()) {
                System.exit(0);
            }
        });
    }
}