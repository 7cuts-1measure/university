package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;

public class ScoreArea extends JPanel {
    Model model;
    JLabel scoreText;
    JLabel scoreValue;

    public ScoreArea(Rectangle bounds, Model model) {
        super();
        scoreText = new JLabel("Score:");
        scoreText.setForeground(Color.WHITE);
        add(scoreText);

        scoreValue = new JLabel("");
        scoreValue.setForeground(Color.WHITE);
        add(scoreValue);
        // set constructor params
        this.model = model;
        this.setBounds(bounds);

        this.setBackground(Color.BLACK);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        scoreValue.setText(String.valueOf(model.getScore()));
    }
}
