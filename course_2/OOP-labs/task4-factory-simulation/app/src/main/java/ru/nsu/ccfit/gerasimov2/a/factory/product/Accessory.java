package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Accessory implements Product {
    private final int id;

    public Accessory(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

}
