package ru.nsu.ccfit.gerasimov2.a.factory.product;

public abstract class BaseProduct {
    private int id;

    public int getID() {
        return id;
    }

    public BaseProduct(int id) {
        this.id = id;
    }
}
