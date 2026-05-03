package ru.nsu.ccfit.gerasimov2.a.factory.product;

public class Car extends BaseProduct {

    public Car(int id, Body body, Motor motor, Accessory accessory) {
        super(id);
        if (body == null || motor == null || accessory == null) 
            throw new NullPointerException("Cannot assemble car");
    }
}
