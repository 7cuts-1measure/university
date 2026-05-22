package client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import common.protocol.Datagram;
import common.protocol.ObjectProtocol;
import common.protocol.Protocol;
import common.protocol.UnsupportedProtocolException;
import common.request.LoginRequest;
import common.request.LogoutRequest;
import common.request.MessageRequest;
import common.response.ErrorResponse;
import common.response.LoginResponse;
import common.response.LogoutResponse;
import common.response.MessageResponse;
import common.response.Response;

import static java.lang.System.in;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainChatWindow extends JFrame {
    private final String nickname;
    private JTextArea chatArea;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JTextField inputField;
    private JButton sendButton;
    private JButton disconnectButton;

    private Socket socket = null;

    private Protocol protocol = null;

    private String sessionId = null;

    private NetworkManager networkManager = null;

    public MainChatWindow(String nickname) {
        this.nickname = nickname;
        setTitle("Чат – " + nickname);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                dispose();
            }
        });

        initComponents();
    }

    private void initComponents() {
        // Центральная панель: чат + список пользователей
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Область чата с прокруткой
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(chatScroll, BorderLayout.CENTER);

        // Список пользователей справа
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFixedCellWidth(150);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(BorderFactory.createTitledBorder("Пользователи"));
        mainPanel.add(userScroll, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Нижняя панель с полем ввода и кнопками
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        inputField = new JTextField();
        inputField.addActionListener(this::onSendMessage);
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sendButton = new JButton("Отправить");
        sendButton.addActionListener(this::onSendMessage);
        disconnectButton = new JButton("Отключиться");
        disconnectButton.addActionListener(e -> disconnect());
        buttonPanel.add(sendButton);
        buttonPanel.add(disconnectButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // === МЕТОДЫ-ЗАГЛУШКИ ДЛЯ СЕТЕВОЙ ЛОГИКИ ===

    public void connect(String host, int port, String username) {

        try {
            socket = new Socket(host, port);
            protocol = new ObjectProtocol(socket.getInputStream(), socket.getOutputStream());
            
            networkManager = new NetworkManager(protocol);
            LoginRequest loginRequset = new LoginRequest(username, "OBJECT");
            protocol.sendDatagram(loginRequset);
            
            Datagram response = protocol.receiveDatagram();
            if (response instanceof ErrorResponse) {
                System.out.println("ERROR: " + ((ErrorResponse) response).getReason());
                return;
            } else if (response instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse) response;
                
                sessionId = loginResponse.getSessionId();
            } else {
                System.out.println("Invalid Response");
                return;
            }

        } catch (UnsupportedProtocolException | IOException e) {
            e.printStackTrace();
            return;
        }


        addMessageToChat("*** Подключение к " + host + ":" + port + " ***");
    }

    public void sendMessage(String text) {
        if (text.isEmpty()) return;

        MessageRequest mr = new MessageRequest(sessionId, text);

        Response response = networkManager.requsetAndGetResponse(mr);
        
        if (response instanceof ErrorResponse) {
            printError((ErrorResponse) response);
        } else if (response instanceof MessageResponse) {
            // success
        } else {
            System.out.println("Invalid response");
        }

        inputField.setText("");
    }

    private void printError(ErrorResponse response) {
        System.err.println("ERROR: " + response.getReason());
    }

    public boolean disconnect() {
        LogoutRequest lr = new LogoutRequest(sessionId);

        Response response = networkManager.requsetAndGetResponse(lr);

        if (response instanceof ErrorResponse) {
            printError((ErrorResponse) response);
        } else if (response instanceof LogoutResponse) {
            // success
            addMessageToChat("*** Отключение от чата ***");
            // Закрыть окно через секунду (или сразу)
            SwingUtilities.invokeLater(() -> {
                setVisible(false);
                dispose();
                // Показать окно логина заново
                LoginDialog login = new LoginDialog(null);
                login.setVisible(true);
            });
            return true;
        }
        return false;
    }

    // Методы для обновления GUI (должны вызываться из EDT)
    public void addMessageToChat(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            // Автопрокрутка вниз
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public void updateUserList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        });
    }

    // Обработчики событий
    private void onSendMessage(ActionEvent e) {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            sendMessage(text);
        }
    }
}
