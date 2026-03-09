package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameForm extends JFrame {
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
        setBackground(Color.BLACK);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        gameArea = new GameArea(new Rectangle(50, 50, 400, 400), model);
        add(gameArea);

        scoreArea = new ScoreArea(new Rectangle(450, 50, 200, 100), model);
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
