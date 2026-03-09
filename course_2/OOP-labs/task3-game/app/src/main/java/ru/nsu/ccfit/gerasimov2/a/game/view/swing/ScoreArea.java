package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;

public class ScoreArea extends JPanel {
    Model model;
    Label scoreText;
    Label scoreValue;

    public ScoreArea(Rectangle bounds, Model model) {
        super();
        scoreText = new Label("Score:");
        add(scoreText);

        scoreValue = new Label(null);
        add(scoreValue);
        // set constructor params
        this.model = model;
        this.setBounds(bounds);

        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        scoreValue.setText(Integer.valueOf(model.getScore()).toString());
    }
}
