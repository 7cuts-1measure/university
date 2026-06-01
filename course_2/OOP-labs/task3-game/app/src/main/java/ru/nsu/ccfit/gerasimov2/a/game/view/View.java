package ru.nsu.ccfit.gerasimov2.a.game.view;

import ru.nsu.ccfit.gerasimov2.a.game.model.Position;

import ru.nsu.ccfit.gerasimov2.a.game.Observer;
import ru.nsu.ccfit.gerasimov2.a.game.controller.Controller;

public interface View extends Observer {

    public void setController(Controller controller);
    public Controller getController();

    public void update();
    public void message(String string);;
    public void popupMessage(String string);
    public void drawSelection(Position selectionPos);
    public void start();
}
