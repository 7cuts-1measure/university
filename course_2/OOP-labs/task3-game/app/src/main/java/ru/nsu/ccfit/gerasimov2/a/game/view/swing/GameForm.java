package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.border.Border;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameForm extends JFrame {
    final static Color bgColor = new Color(35, 35, 35);

    private int height;
    private GameArea gameArea;
    private ScoreArea scoreArea;

    public GameForm(String winTitle, int width, int heght, Model model) {
        super(winTitle);
        setSize(width, height);
        setLocationRelativeTo(null);

        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);

        gameArea = new GameArea(model);
        add(gameArea, BorderLayout.CENTER);

        scoreArea = new ScoreArea(new Rectangle(gameArea.getX() + 400, 50, 200, 100), model);
        add(scoreArea, BorderLayout.NORTH);

        pack();

        setSize(width, heght);
        setResizable(true);

        setLocationRelativeTo(null);
        setVisible(true);
    }
    public Position getSelection() {
        return gameArea.getSelection();
    }
    public void drawSelection(Position selectionPos) {
        gameArea.setSelection(selectionPos);
    }

}
