package simulation.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Font;


public class SimulationWindow {
        private JFrame frame;
    private JTextArea textArea;
    private JScrollPane scrollPane;

    public SimulationWindow() {
        // Создаём окно
        frame = new JFrame("Симуляция фабрики");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // по центру экрана

        // Текстовая область для вывода сообщений
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Добавляем прокрутку
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Метод для добавления сообщения из модели
    public void appendMessage(String message) {
        // Swing требует обновления UI в потоке EDT
        SwingUtilities.invokeLater(() -> {
            textArea.append(message + "\n");
            // Автопрокрутка вниз
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
    
    // Если нужно очистить окно
    public void clear() {
        SwingUtilities.invokeLater(() -> textArea.setText(""));
    }
}
