package ru.nsu.ccfit.gerasimov2.a.game.model;

import java.io.IOException;
import java.io.ObjectOutputStream;


public class GameStateManager {
    public void saveModel(GameModel model) throws SaveException {
        try {
            ObjectOutputStream os = new ObjectOutputStream(System.out);
            // TOOD: does it really so simple
            os.writeObject(model);
        } catch (IOException e) {
            System.err.println("Cannot save game state");
            throw new SaveException();
        }
    }
}