package ru.nsu.ccfit.gerasimov2.a.factory.product;

public abstract class Product {
    private int id;

    public int getID() {
        return id;
    }

    public Product(int id) {
        this.id = id;
    }
}
