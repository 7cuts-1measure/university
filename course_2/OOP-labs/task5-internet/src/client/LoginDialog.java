package client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {
    private JTextField hostField;
    private JTextField portField;
    private JTextField nicknameField;
    private JButton connectButton;
    private boolean succeeded = false;

    public LoginDialog(JFrame parent) {
        super(parent, "Подключение к чату", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Поле для адреса сервера
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Сервер:"), gbc);
        gbc.gridx = 1;
        hostField = new JTextField("localhost", 15);
        add(hostField, gbc);

        // Поле для порта
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Порт:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField("6969", 15);
        add(portField, gbc);

        // Поле для ника
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Никнейм:"), gbc);
        gbc.gridx = 1;
        nicknameField = new JTextField(15);
        add(nicknameField, gbc);

        // Кнопка подключения
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        connectButton = new JButton("Подключиться");
        connectButton.addActionListener(this::onConnect);
        add(connectButton, gbc);

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void onConnect(ActionEvent e) {
        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        String nickname = nicknameField.getText().trim();

        if (host.isEmpty() || portText.isEmpty() || nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Порт должен быть числом");
            return;
        }

        // ЗДЕСЬ БУДЕТ РЕАЛЬНОЕ ПОДКЛЮЧЕНИЕ
        // Пока заглушка – вызываем метод connect у главного окна
        MainChatWindow mainWindow = new MainChatWindow(nickname);
        mainWindow.connect(host, port, nickname);  // заглушка
        succeeded = true;
        dispose();
        mainWindow.setVisible(true);
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}