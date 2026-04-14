package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Car implements Product{
    private final int id;

    @Override
    public int getID() {
        return id;
    }

    public Car(int id, Body body, Motor motor, Accessory accessory) {
        if (body == null || motor == null || accessory == null) 
            throw new NullPointerException("Cannot assemble car");
        this.id = id;
    }
}
