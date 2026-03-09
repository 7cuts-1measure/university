package ru.nsu.ccfit.gerasimov2.a.game.view;

import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.model.Model;
import ru.nsu.ccfit.gerasimov2.a.game.model.PlayerID;

public interface View {
    void display(Model model);

    int getMaximumPlayers();

    void congrats(List<PlayerID> winner);
}
