package ru.nsu.ccfit.gerasimov2.a.game.model;

import java.util.List;
import java.util.Optional;

public interface Model {
    /**
     * @return total rows in a field
     */
    int getRows();

    /**
     * 
     * @return total columns in a field
     */
    int getCols();

    /**
     * calculate is player being a winner depends on the field state
     * 
     * @param player
     * @return
     */
    boolean isWinner(PlayerID player);

    boolean isGameOver();

    List<PlayerID> getWinners();

    /**
     * 
     * @param owner - new cell owner
     * @param row
     * @param col
     * @return true if change is possible. For example, method will return false
     *         if another player already owns the cell
     */
    boolean changeCellOwner(PlayerID owner, int row, int col);

    /**
     * 
     * @param row
     * @param col
     * @return A player if someone owns the cell
     */
    Optional<PlayerID> getCellOwner(int row, int col);

    boolean isOwned(int row, int col);
}
