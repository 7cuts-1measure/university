package ru.nsu.ccfit.gerasimov2.a.game.view.console;

import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.PlayerID;
import ru.nsu.ccfit.gerasimov2.a.game.view.View;

public class ConsoleView implements View {
    private final char[] signs = { 'X', 'O', '#', '$' };

    @Override
    public void display(Model model) {
        int rows = model.getRows();
        int cols = model.getCols();
        System.out.print("  ");
        for (int i = 0; i < cols; i++) {
            System.out.printf("  %d ", i);
        }
        System.out.print("\n--");
        for (int i = 0; i < cols; i++) {
            System.out.print("-----");
        }

        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.printf("%d | ", i);
            for (int j = 0; j < cols; j++) {
                if (!model.isOwned(i, j)) {
                    System.out.print(' ');
                } else {
                    PlayerID owner = model.getCellOwner(i, j).get();
                    if (owner.getID() > getMaximumPlayers()) {
                        throw new IllegalStateException("Players more then maximum allowed");
                    }
                    System.out.print(signs[owner.getID()]);
                }
                System.out.print(" | ");

            }
            System.out.println();
        }
    }

    @Override
    public int getMaximumPlayers() {
        return signs.length;
    }

    @Override
    public void congrats(List<PlayerID> winners) {
        for (PlayerID palyer : winners) {
            System.out.println("Player " + palyer.getID() + " wins!");
        }
    }

}
