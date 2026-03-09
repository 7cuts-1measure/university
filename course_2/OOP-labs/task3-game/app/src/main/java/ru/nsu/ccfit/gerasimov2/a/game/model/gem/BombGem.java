package ru.nsu.ccfit.gerasimov2.a.game.model.gem;

import ru.nsu.ccfit.gerasimov2.a.game.model.GemField;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public class BombGem extends Gem{
    private final int radius;
    boolean isDestroyed = false;

    public BombGem(int color, int radius) {
        super(color);
        this.radius = radius;
    }


    public BombGem(int color) {
        super(color);
        this.radius = 1;
    }


    @Override
    public void destroyEfffect(GemField gemField, Position pos) {
        if (isDestroyed) return;
        isDestroyed = true;

        int row = pos.getRow();
        int col = pos.getCol();

        // calculate rectangle borders
        int left = Math.max(col - radius, 0);
        int right = Math.min(col + radius + 1, gemField.getCols());

        int up = Math.max(row - radius, 0);
        int down = Math.min(row + radius + 1, gemField.getRows());

        // destroy all gems in the radius
        for (int i = left; i < right; i++) {
            for (int j = up; j < down; j++) {
                Position nearTheBomb = new Position(i, j);
                Gem gemToDestroy = gemField.at(nearTheBomb);
                gemToDestroy.destroyEfffect(gemField, nearTheBomb);
            }
        } 
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed; 
    }

}
