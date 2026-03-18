package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Accessory implements Product {
    private int id = 0;

    public Accessory(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

}
