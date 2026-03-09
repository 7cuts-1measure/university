package ru.nsu.ccfit.gerasimov2.a.game.model.strategy;

import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.model.GemField;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

public interface DestroyStratagy {
    List<Position> getPositionsToDestroy(GemField gemField);

    boolean isDestroyable(GemField gemField);
}
