package ru.nsu.ccfit.gerasimov2.a.game.model.gem;

import ru.nsu.ccfit.gerasimov2.a.game.model.GemField;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class DefaultGem extends Gem {

    private boolean isDestroyed = false;
    
    public DefaultGem(int color) {
        super(color);
    }

    @Override
    public void destroyEfffect(GemField gemField, Position pos) {
        isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

}
