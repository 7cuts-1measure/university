package ru.nsu.ccfit.gerasimov2.a.game.model;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.Observable;
import ru.nsu.ccfit.gerasimov2.a.game.Observer;
import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;

public abstract class GameModel implements Observable {

    List<Observer> observers;

    public GameModel() {
        observers = new ArrayList<>();
    }


    // ============= Model interface ================== //
    public abstract  List<Position> getPositionsToDestroy();

    public abstract  boolean isDestroyable();


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
        for (Observer o : observers) { o.update(); }
    }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    public void notifyView() { notifyObservers(); }


    public abstract boolean setMove(Position p1, Position p2);    
    public abstract void step();

    public abstract GemField getGemField();

    public abstract int getCols();
    public abstract int getRows();

    public abstract AnimationState getAnimationState(); // TODO: AnimationState - enum с состояниями 
    public abstract int getCurrentAnimationStep();
    public abstract int getTotalAnimationSteps();
    public boolean isAnimating;

    public void startSwapAnimation(Position p1, Position p2);  // начало обмена
    public void nextAnimationStep();        // переход на один
}
