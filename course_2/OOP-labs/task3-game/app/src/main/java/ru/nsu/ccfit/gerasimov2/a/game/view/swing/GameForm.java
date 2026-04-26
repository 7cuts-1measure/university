package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameForm extends JFrame {
    final static Color bgColor = new Color(35, 35, 35);

    private int height;
    private GameArea gameArea;
    private ScoreArea scoreArea;
    public GameForm(String winTitle, int width, int heght, ModelBox modelBox, SwingView view) {
        super(winTitle);

        setSize(width, height);
        setLocationRelativeTo(null);

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);

        gameArea = new GameArea(modelBox);
        add(gameArea, BorderLayout.CENTER);

        scoreArea = new ScoreArea(modelBox);
        add(scoreArea, BorderLayout.NORTH);

        JMenuBar menuBar = new MenuBar(modelBox, view);
        setJMenuBar(menuBar);
        
        pack();

        setMinimumSize(getSize());
        setResizable(true);
        setVisible(true);
    }
    
    public GameArea getGameArea() {
        return gameArea;
    }

    public void drawSelection(Position selectionPos) {
        gameArea.setSelection(selectionPos);
        gameArea.repaint();
    }

    public void setController(Controller controller) {
        gameArea.setController(controller);
    }

    public ScoreArea getScoreArea() { 
        return scoreArea;
    }


}
