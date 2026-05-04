package simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

import javax.swing.JPanel;


public class BlockComponent extends JPanel {
    private static final Color TITLE_COLOR = Color.BLACK;
    
    private static final Color MESSAGE_COLOR = Color.BLUE;

    private static final Color BORDER_COLOR = Color.BLACK;

    private String title;      // название блока (внизу)
    private String message;    // сообщение (по центру)

    public BlockComponent(String title, String message) {
        this.title = title;
        this.message = message;
        setPreferredSize(new Dimension(200, 150));
        setBackground(Color.WHITE);
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    public void setMessage(String message) {
        this.message = message;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        drawBorder(g2d);
        int lineY = drawLowerLine(g2d);
        drowTitleAtBottom(g2d, lineY);

        drawMessageCentered(g2d, lineY);
    }

    private void drawMessageCentered(Graphics2D g2d, int lineY) {
        int w = getWidth();
        if (message != null && !message.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int msgWidth = fm.stringWidth(message);
            int msgX = (w - msgWidth) / 2;

            // Центрируем по вертикали между верхом блока и чертой
            int centerAreaY = (lineY - 20) / 2 + 10;
            int msgY = centerAreaY + fm.getAscent() / 2;
            g2d.setColor(MESSAGE_COLOR);
            g2d.drawString(message, msgX, msgY);
        }
    }

    private void drowTitleAtBottom(Graphics2D g2d, int lineY) {
        int w = getWidth();
        if (title != null && !title.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            int titleX = (w - titleWidth) / 2;
            int titleY = lineY + 20;   // небольшой отступ от черты
            g2d.setColor(TITLE_COLOR);
            g2d.drawString(title, titleX, titleY);
        }
    }

    private int drawLowerLine(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();
        int lineY = h - 30;
        g2d.drawLine(10, lineY, w - 10, lineY);
        return lineY;
    }

    private void drawBorder(Graphics2D g2d) {
        int w = getWidth();
        int h = getHeight();
        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(0, 0, w - 1, h - 1);
    }
}
