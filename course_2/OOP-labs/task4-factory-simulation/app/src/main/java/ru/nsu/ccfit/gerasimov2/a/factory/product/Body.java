package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Body implements FactoryProduct {
    private int id;
    
    @Override
    public int getID() {
        return id;
    }
}
