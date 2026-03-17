package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public class ScoreArea extends JPanel {
    GameModel model;
    JLabel scoreText;
    JLabel scoreValue;

    public ScoreArea(GameModel model) {
        setMinimumSize(new Dimension(100, 50));
        setPreferredSize(new Dimension(100, 50));
        Font font = new Font(null, Font.BOLD, 24);
        scoreText = new JLabel("Score:");
        scoreText.setForeground(Color.WHITE);
        scoreText.setFont(font);
        add(scoreText);

        scoreValue = new JLabel("");
        scoreValue.setForeground(Color.WHITE);
        scoreValue.setFont(font);
        add(scoreValue);

        this.model = model;

        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.black, 2));
        
    }

    public void updateScore() {
        scoreValue.setText(String.valueOf(model.getScore()));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        scoreValue.setText(String.valueOf(model.getScore()));
    }
}
