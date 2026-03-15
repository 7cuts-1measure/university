package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameForm extends JFrame {
    final static Color bgColor = new Color(35, 35, 35);

    private int width, height;
    private Model model;
    private GameArea gameArea;
    private ScoreArea scoreArea;

    public GameForm(String winTitle, int width, int heght, Model model) {
        super(winTitle);
        this.width = width;
        this.height = heght;
        this.model = model;
        setSize(width, height);
        setLocationRelativeTo(null);

        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);

        gameArea = new GameArea(new Rectangle(50, 50, width, heght), model);
        add(gameArea);

        scoreArea = new ScoreArea(new Rectangle(gameArea.getX() + 400, 50, 200, 100), model);
        add(scoreArea);

        setVisible(true);
    }
    public Position getSelection() {
        return gameArea.getSelection();
    }
    public void drawSelection(Position selectionPos) {
        gameArea.setSelection(selectionPos);
    }

}
