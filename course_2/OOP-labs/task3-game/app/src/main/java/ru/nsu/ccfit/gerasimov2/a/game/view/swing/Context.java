package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import java.awt.Font;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public class Context {
    GameModel model;
    Font font;
    public Context(GameArea gameArea, GameModel model, Font font) {
        this.model = model;
        this.font = font;
    }
}
