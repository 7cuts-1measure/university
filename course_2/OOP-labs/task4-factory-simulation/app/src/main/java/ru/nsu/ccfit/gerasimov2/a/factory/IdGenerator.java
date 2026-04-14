package ru.nsu.ccfit.gerasimov2.a.factory;

public class IdGenerator {
    private int count = 0;

    public int next() {
        return count++;
    }
}
