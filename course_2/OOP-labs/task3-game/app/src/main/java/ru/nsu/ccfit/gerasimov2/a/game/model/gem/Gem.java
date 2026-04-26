package ru.nsu.ccfit.gerasimov2.a.game.model.gem;

import ru.nsu.ccfit.gerasimov2.a.game.model.GemField;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public abstract class Gem implements java.io.Serializable {
    public final int color;

    public abstract void destroyEfffect(GemField gemField, Position pos);
    public abstract boolean isDestroyed();

    public Gem(int color) {
        this.color = color;
    }
}
