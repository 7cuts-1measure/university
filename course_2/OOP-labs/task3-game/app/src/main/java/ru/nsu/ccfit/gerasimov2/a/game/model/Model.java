package ru.nsu.ccfit.gerasimov2.a.game.model;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.Observable;
import ru.nsu.ccfit.gerasimov2.a.game.Observer;
import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;

public abstract class Model implements Observable {

    List<Observer> observers;

    public Model() {
        observers = new ArrayList<>();
    }


    // ============= Model interface ================== //
    public abstract  List<Position> getPositionsToDestroy();

    public abstract  boolean isDestroyable();

    public abstract GemField getGemField();

    public abstract Gem gemAt(Position pos);

    public abstract Gem gemAt(int row, int col);

    public abstract int getScore(); 

    // =============== Observer pattern methods ================= //
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) { o.updateSuspended(); }
    }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    public void notifyView() { notifyObservers(); }


    public abstract boolean setMove(Position p1, Position p2);    
    public abstract void step();
}
