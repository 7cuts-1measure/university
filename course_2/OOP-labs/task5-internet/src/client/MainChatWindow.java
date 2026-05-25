package client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import common.event.ChatMessageEvent;
import common.event.Event;
import common.event.UserConnectedEvent;
import common.event.UserDisconnectedEvent;
import common.protocol.ConnectionLostException;
import common.protocol.Protocol;
import common.protocol.XmlProtocol;
import common.request.LoginRequest;
import common.request.LogoutRequest;
import common.request.MessageRequest;
import common.response.ErrorResponse;
import common.response.LoginResponse;
import common.response.LogoutResponse;
import common.response.MessageResponse;
import common.response.Response;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class MainChatWindow extends JFrame {
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

    private Thread pingerThread = null;

    public MainChatWindow(String nickname) {
        setTitle("Чат – " + nickname);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
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
        inputField.addActionListener(this::onSendButtomClick);
        bottomPanel.add(inputField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sendButton = new JButton("Отправить");
        sendButton.addActionListener(this::onSendButtomClick);
        disconnectButton = new JButton("Отключиться");
        disconnectButton.addActionListener(e -> disconnect());
        buttonPanel.add(sendButton);
        buttonPanel.add(disconnectButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);    }


    private void processUserDisconnectedEvent(UserDisconnectedEvent event) {
        addMessageToChat("*** User " + event.getUserName() + " disconnected, reason: " + event.getReason() +  " ***");
    }

    private void processUserConnectedEvent(UserConnectedEvent event) {
        addMessageToChat("*** User " + event.getUserName() + " is online!***");
    }

    private void processChatMessageEvent(ChatMessageEvent chatMessageEvent) {
        addMessageToChat(chatMessageEvent.getFrom() + ": " + chatMessageEvent.getText());
    }

    public void connect(String host, int port, String username) throws ConnectionException {

        try {
            socket = new Socket(host, port);
            //protocol = new ObjectProtocol(socket.getInputStream(), socket.getOutputStream());
            protocol = new XmlProtocol(socket.getInputStream(), socket.getOutputStream());

            networkManager = new NetworkManager(protocol);

            networkManager.startEventListener(new EventListener() {
                @Override
                public void onEvent(Event event) {
                    if (event instanceof ChatMessageEvent) {
                        processChatMessageEvent((ChatMessageEvent) event);
                    } else if (event instanceof UserConnectedEvent) {
                        processUserConnectedEvent((UserConnectedEvent) event);
                    } else if (event instanceof UserDisconnectedEvent) {
                        processUserDisconnectedEvent((UserDisconnectedEvent) event);
                    }
                }
                @Override
                public void onConnectionLost() {
                    pingerThread.interrupt();
                    goToLoginWindow();
                }         
            });
            
            LoginRequest loginRequset = new LoginRequest(username, "OBJECT");
            Response response = networkManager.doRequsetAndWaitResponse(loginRequset);
            if (response instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) response;
                System.out.println("ERROR: " + errorResponse.reason);
                throw new ConnectionException(errorResponse.reason);
            } else if (response instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse) response;
                sessionId = loginResponse.sessionId;
            } else {
                System.out.println("Invalid Response");
                throw new ConnectionException("Invalid Response");
            }
        
            pingerThread = new PingerThread(sessionId, networkManager);
            pingerThread.start();
            

        } catch (InterruptedException | IOException | ConnectionLostException e) {
            throw new ConnectionException(e.getLocalizedMessage());
        }

        addMessageToChat("*** Подключение к " + host + ":" + port + " ***");
        

    }

    public void sendMessage(String text) {
        if (text.isEmpty()) return;

        MessageRequest mr = new MessageRequest(sessionId, text);

        Response response;
        try {
            response = networkManager.doRequsetAndWaitResponse(mr);
            if (response instanceof ErrorResponse) {
                printError((ErrorResponse) response);
            } else if (response instanceof MessageResponse) {
                // success. nothing to do.
            } else {
                System.out.println("Invalid response");
            }
        } catch (ConnectionLostException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }    
        inputField.setText("");
    }

    private void printError(ErrorResponse response) {
        System.err.println("ERROR: " + response.reason);
    }

    public boolean disconnect() {
        LogoutRequest logoutRequest = new LogoutRequest(sessionId);

        if (pingerThread != null) {
            pingerThread.interrupt();
        }
        try {
            Response response = networkManager.doRequsetAndWaitResponse(logoutRequest);

            if (response instanceof ErrorResponse) {
                printError((ErrorResponse) response);
            } else if (response instanceof LogoutResponse) {
                goToLoginWindow();
                return true;
            }
        } catch (ConnectionLostException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    private void goToLoginWindow() {
        // success
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
            // Показать окно логина заново
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
        });
    }

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

    private void onSendButtomClick(ActionEvent e) {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            sendMessage(text);
        }
    }
}
