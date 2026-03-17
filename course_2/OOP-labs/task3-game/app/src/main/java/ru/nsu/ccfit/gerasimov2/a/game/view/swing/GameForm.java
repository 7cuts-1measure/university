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
        Font font = new Font("Arial", Font.BOLD, 16);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.BLACK);        
        // Создаем меню "Игра"
        JMenu gameMenu = new JMenu("Игра");
        gameMenu.setForeground(Color.WHITE);
        gameMenu.setBackground(Color.BLACK);
        gameMenu.setFont(font);
        
        JMenuItem newGameItem = new JMenuItem("New game");
        newGameItem.setForeground(Theme.WHITE);
        newGameItem.setBackground(Color.BLACK);
        newGameItem.setFont(font);
        newGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New game started!");
                gameArea.setVisible(true);
                model.reset();
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit to Windows");
        exitItem.setForeground(Theme.WHITE);
        exitItem.setBackground(Color.BLACK);
        exitItem.setFont(font);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Завершает игру
            }
        });

        gameMenu.add(newGameItem);
        gameMenu.add(exitItem);
        
        menuBar.add(gameMenu);
        
        setJMenuBar(menuBar);
    }

}
