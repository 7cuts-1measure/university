package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Body implements Product {
    private final int id;
    
    @Override
    public int getID() {
        return id;
    }

    public Body(int id) {
        this.id = id;
    }
}
