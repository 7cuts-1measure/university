package ru.nsu.ccfit.gerasimov2.a.game.view;

import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

import ru.nsu.ccfit.gerasimov2.a.game.Observer;
import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;
import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public abstract class View implements Observer {
    protected GameModel model;
    protected Controller controller;

    public View(GameModel model) {
        this.model = model;
    }

    public abstract void setController(Controller controller);

    public abstract void update();
    public abstract void message(String string);;
    public abstract void popupMessage(String string);
    public abstract void drawSelection(Position selectionPos);
    public abstract void start();
}
