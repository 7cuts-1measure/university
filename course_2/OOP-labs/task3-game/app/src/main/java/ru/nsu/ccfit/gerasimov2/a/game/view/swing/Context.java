package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Font;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public class Context {
    GameArea gameArea;
    GameModel model;
    Font font;
    public Context(GameArea gameArea, GameModel model, Font font) {
        this.gameArea = gameArea;
        this.model = model;
        this.font = font;
    }

    
}
