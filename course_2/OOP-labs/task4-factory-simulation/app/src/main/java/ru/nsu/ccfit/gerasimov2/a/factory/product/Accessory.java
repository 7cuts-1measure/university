package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Accessory implements FactoryProduct {
    private int id = 0;

    @Override
    public int getID() {
        return id;
    }

}
