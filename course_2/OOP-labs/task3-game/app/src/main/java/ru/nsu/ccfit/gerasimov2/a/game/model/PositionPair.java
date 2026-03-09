package ru.nsu.ccfit.gerasimov2.a.game.model;

public class PositionPair {
    private Position first;
    private Position second;
    
    
    public PositionPair(Position first, Position second) {
        this.first = first;
        this.second = second;
    }

    public Position getFirst() {
        return first;
    }
    public void setFirst(Position first) {
        this.first = first;
    }
    public Position getSecond() {
        return second;
    }
    public void setSecond(Position second) {
        this.second = second;
    }

}