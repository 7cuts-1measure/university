package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class GameForm extends JFrame {
    final static Color bgColor = new Color(35, 35, 35);

    private int height;
    private GameArea gameArea;
    private ScoreArea scoreArea;
    private GameModel model;

    public GameForm(String winTitle, int width, int heght, GameModel model) {
        super(winTitle);

        this.model = model;
        setSize(width, height);
        setLocationRelativeTo(null);

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);

        gameArea = new GameArea(model);
        add(gameArea, BorderLayout.CENTER);

        scoreArea = new ScoreArea(model);
        add(scoreArea, BorderLayout.NORTH);

        createMenuBar();

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

    private void createMenuBar() {
        JMenuBar menuBar = new MenuBar(gameArea, model);
        setJMenuBar(menuBar);
    }

}
