package ru.nsu.ccfit.gerasimov2.a.game.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.nsu.ccfit.gerasimov2.a.game.Observable;
import ru.nsu.ccfit.gerasimov2.a.game.Observer;
import ru.nsu.ccfit.gerasimov2.a.game.model.gem.Gem;

// TODO: check if i need implement methods form this interface
public abstract class GameModel implements Observable, java.io.Serializable {

    List<Observer> observers;

    public GameModel() {
        observers = new ArrayList<>();
    }

    // +++++++++++++++++++++++ Geters +++++++++++++++++++++
    public abstract Gem gemAt(int row, int col);
    public abstract Gem gemAt(Position currPos);
    public abstract int getScore(); 
    public abstract boolean checkMove(Position p1, Position p2);    
    public abstract int getCols();
    public abstract int getRows();
    // ----------------------------------------------------

    // +++++++++++++ Change model state ++++++++++++++++
    public abstract AnimationState getAnimationState();
    public abstract void startSwapAnimation(Position p1, Position p2);  // начало обмена
    public abstract void nextAnimationStep();        // переход на один
    public abstract void restart();
    public abstract void reset();
    public abstract boolean isAnimating();  // TODO whatis it

    public final void saveUserResult(String username) throws IOException {
        var userResult = new UserResult(username, getScore());
        var saver = new UserResultFileManager();
        saver.save(userResult);
    }

    public final List<UserResult> loadUserResults() throws IOException {
        var loader = new UserResultFileManager();
        return loader.load();    
    }
    // ------------------------------------------------------


    

    // +++++++++++++++++ Observer pattern methods ++++++++++++++++++
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) { observers.remove(o); }

    protected void notifyObservers() {
        for (Observer o : observers) { o.update(); }
    }

    protected void notifyView() { notifyObservers(); }
    //----------------------------------------------------------------
    
}
