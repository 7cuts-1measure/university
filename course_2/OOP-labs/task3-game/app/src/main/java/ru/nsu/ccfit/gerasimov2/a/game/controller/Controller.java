package ru.nsu.ccfit.gerasimov2.a.game.controller;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public abstract class Controller{
    protected View view;
    protected Model model;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        model.addObserver(view);
    }

    abstract public void runGame();
}
