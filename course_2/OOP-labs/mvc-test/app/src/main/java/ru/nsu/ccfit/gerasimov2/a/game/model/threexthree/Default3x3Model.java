package ru.nsu.ccfit.gerasimov2.a.game.model.threexthree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.PlayerID;

public class Default3x3Model implements Model {

    public final int rows = 3;
    public final int cols = 3;
    private List<PlayerID> winners;
    private boolean isGameOver;

    public Default3x3Model() {
        this.field = new PlayerID[rows][cols];
    }

    public boolean isGameOver() {
        if (isGameOver) { /* do not need to find winners because somebody already win */
            return true;
        }

        winners = getWinners();
        if (!winners.isEmpty()) {
            isGameOver = true;
        }
        return isGameOver;
    }

    private PlayerID[][] field;

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public boolean isWinner(PlayerID player) {
        /* здесь надо, чтобы 3 клетки подряд имели одинакового владельца */
        throw new UnsupportedOperationException("Unimplemented method 'isWinner'");
    }

    @Override
    public boolean changeCellOwner(PlayerID owner, int row, int col) {
        if (isOwned(row, col)) {
            return false;
        }
        field[row][col] = owner;
        return true;
    }

    @Override
    public Optional<PlayerID> getCellOwner(int row, int col) {
        return Optional.ofNullable(field[row][col]);
    }

    @Override
    public boolean isOwned(int row, int col) {
        return field[row][col] != null;
    }

    @Override
    public List<PlayerID> getWinners() {
        if (isGameOver) {
            return winners;
        }

        winners = new ArrayList<PlayerID>();

        // TODO: rewrite this for common case (not only 3x3 field)

        // because it is 3x3 model we can just check it manually
        // check 3 rows
        if (field[0][0] != null && field[0][1] != null && field[0][2] != null
                && field[0][0].getID() == field[0][1].getID() && field[0][1].getID() == field[0][2].getID()) {
            winners.add(field[0][0]);
        }
        if (field[1][0] != null && field[1][1] != null && field[1][2] != null
                && field[1][0].getID() == field[1][1].getID() && field[1][1].getID() == field[1][2].getID()) {
            winners.add(field[1][0]);
        }
        if (field[2][0] != null && field[2][1] != null && field[2][2] != null
                && field[2][0].getID() == field[2][1].getID() && field[2][1].getID() == field[2][2].getID()) {
            winners.add(field[2][0]);
        }

        // check 3 columns
        if (field[0][0] != null && field[1][0] != null && field[2][0] != null
                && field[0][0].getID() == field[1][0].getID() && field[1][0].getID() == field[2][0].getID()) {
            winners.add(field[0][0]);
        }
        if (field[0][1] != null && field[1][1] != null && field[2][1] != null
                && field[0][1].getID() == field[1][1].getID() && field[1][1].getID() == field[2][1].getID()) {
            winners.add(field[0][1]);
        }
        if (field[0][2] != null && field[1][2] != null && field[2][2] != null
                && field[0][2].getID() == field[1][2].getID() && field[1][2].getID() == field[2][2].getID()) {
            winners.add(field[0][2]);
        }

        // check 2 diagoanals
        if (field[0][0] != null && field[1][1] != null && field[2][2] != null
                && field[0][0].getID() == field[1][1].getID() && field[1][1].getID() == field[2][2].getID()) {
            winners.add(field[0][0]);
        }
        if (field[0][2] != null && field[1][1] != null && field[2][0] != null
                && field[0][2].getID() == field[1][1].getID() && field[1][1].getID() == field[2][0].getID()) {
            winners.add(field[0][2]);
        }

        return winners;
    }

}
