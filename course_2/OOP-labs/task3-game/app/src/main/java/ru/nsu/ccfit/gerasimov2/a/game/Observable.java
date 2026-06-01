package ru.nsu.ccfit.gerasimov2.a.game;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void removeAllObservers();
    // void notifyObservers();   
}
