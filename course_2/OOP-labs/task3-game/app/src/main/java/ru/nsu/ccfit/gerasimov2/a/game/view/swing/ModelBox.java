package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public class ModelBox {
    private GameModel model;

    public ModelBox (GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

    public void setModel(GameModel model) {
        this.model = model;
    }
}
