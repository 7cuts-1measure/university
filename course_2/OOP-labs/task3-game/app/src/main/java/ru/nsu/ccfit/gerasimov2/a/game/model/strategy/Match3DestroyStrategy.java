package ru.nsu.ccfit.gerasimov2.a.game.model.strategy;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.model.GemField;
import ru.nsu.ccfit.gerasimov2.a.game.model.Position;
import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;

public class Match3DestroyStrategy implements DestroyStratagy {

    public List<Position> getPositionsToDestroy(GemField gemField) {
        List<Position> horizontal = implGetPositionToDestroy(gemField);

        gemField.transpose();
        List<Position> vertical = implGetPositionToDestroy(gemField);
        gemField.transpose();
        for (Position position : vertical) {
            // transpose indexes back
            var row = position.getRow();
            var col = position.getCol();
            position.setRow(col);
            position.setCol(row);
        }

        horizontal.addAll(vertical);
        return horizontal;

    }

    private List<Position> implGetPositionToDestroy(GemField gemField) {
        final int rows = gemField.getRows();
        final int cols = gemField.getCols();

        List<Position> gemsToDestroy = new ArrayList<Position>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols;) {
                Position currPos = new Position(i, j);
                Gem currGem = gemField.at(currPos);
                List<Position> sameColor = collectSameColorOnRow(gemField, currGem, currPos);
                int count = sameColor.size();
                if (count >= 3) {
                    gemsToDestroy.addAll(sameColor);
                }

                j += count;
            }
        }

        return gemsToDestroy;
    }

    private List<Position> collectSameColorOnRow(GemField gemField, Gem baseGem, Position basePos) {
        final int baseRow = basePos.getRow();
        final int baseCol = basePos.getCol();

        List<Position> sameColor = new ArrayList<>();
        for (int i = baseCol; i < gemField.getCols(); i++) {
            Position currPos = new Position(baseRow, i);
            Gem currGem = gemField.at(currPos);
            if (currGem.color == baseGem.color) {
                sameColor.add(currPos);
            } else {
                break;
            }
        }
        return sameColor;
    }

    @Override
    public boolean isDestroyable(GemField gemField) {
        return !getPositionsToDestroy(gemField).isEmpty();
    }

}
