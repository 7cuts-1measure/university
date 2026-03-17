package ru.nsu.ccfit.gerasimov2.a.game.controller;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public abstract class Controller{
    protected View view;
    protected GameModel model;

    public Controller(GameModel model, View view) {
        this.model = model;
        this.view = view;
        model.addObserver(view);
    }

    public abstract void handleInput(Position userSelection);

    public abstract void resetModel();
}
