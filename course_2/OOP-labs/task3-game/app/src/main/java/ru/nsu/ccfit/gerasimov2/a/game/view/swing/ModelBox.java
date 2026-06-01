package ru.nsu.ccfit.gerasimov2.a.game.view.swing;

import ru.nsu.ccfit.gerasimov2.a.game.model.GameModel;

public class ModelBox {
    private GameModel model;
    private Object lock = new Object();

    public ModelBox (GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        synchronized (lock) {
            return model;
        }
    }

    public void setModel(GameModel model) {
        synchronized (lock) {
            this.model = model;
        }
    }
}
