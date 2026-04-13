package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Font;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public class MenuContext {
    public GameArea gameArea;
    public GameModel model;
    public Font font;


    public MenuContext(GameArea gameArea, GameModel model, Font font) {
        this.gameArea = gameArea;
        this.model = model;
        this.font = font;
    }


    public void gameOver() {
        gameArea.setVisible(false);
        //model.saveUserResult();
        model.reset();
    } 
}
