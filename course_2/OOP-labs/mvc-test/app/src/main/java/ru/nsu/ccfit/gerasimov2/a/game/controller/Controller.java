package ru.nsu.ccfit.gerasimov2.a.game.controller;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

/**
 * Controller interacts with user, telling Model what to do
 * and telling view whet it should draw the model
 */
public abstract class Controller {
    protected View view;
    protected Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    public abstract void updateModel();

    public abstract void run();
}