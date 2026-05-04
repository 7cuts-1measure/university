package simulation.model.factory.product;

public class Car extends Product {
    private final Body body;
    private final Motor motor;
    private final Accessory accessory;

    public Car(int id, Body body, Motor motor, Accessory accessory) {
        super(id);
        this.body = body;
        this.motor = motor;
        this.accessory = accessory;        
    }

    public int getBodyId() {
        return body.getId();
    }

    public int getMotorId() {
        return motor.getId();
    }

    public int getAccessoryId() {
        return accessory.getId();
    }

    @Override
    public String getName() {
        return "Name";
    }
}
