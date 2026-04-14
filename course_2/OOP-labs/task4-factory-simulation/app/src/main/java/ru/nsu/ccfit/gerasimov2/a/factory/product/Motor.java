package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Motor implements Product {
    private final int id;

    @Override
    public int getID() {
        return id;
    }
    
    public Motor(int id) {
        this.id = id;
    }
}
