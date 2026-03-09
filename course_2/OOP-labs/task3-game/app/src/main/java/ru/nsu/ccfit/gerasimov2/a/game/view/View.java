package ru.nsu.ccfit.gerasimov2.a.game.view;

import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

import ru.nsu.ccfit.gerasimov2.a.game.Observer;
import ru.nsu.ccfit.gerasimov2.a.game.model.Model;

public abstract class View implements Observer {
    protected Model model;

    public View(Model model) {
        this.model = model;
    }

    public abstract void updateSuspended();
    public abstract void updateImmediatly();
    public abstract void message(String string);;
    public abstract void popupMessage(String string);
    public abstract Position getUserInputSelection();

    public abstract void drawSelection(Position selectionPos);
    
}
