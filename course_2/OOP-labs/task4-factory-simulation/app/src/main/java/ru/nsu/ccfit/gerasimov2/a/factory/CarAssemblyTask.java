package ru.nsu.ccfit.gerasimov2.a.factory;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Car;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;

public class CarAssemblyTask implements Runnable {

    Storage<Motor> motorStorage;
    Storage<Body> bodyStorage;
    Storage<Accessory> accessoryStorage;
    Storage<Car> carStorage;
    IdGenerator idGenerator = new IdGenerator();

    public CarAssemblyTask(Storage<Motor> motorStorage, Storage<Body> bodyStorage,
            Storage<Accessory> accessoryStorage, Storage<Car> carStorage) {
        this.motorStorage     = motorStorage;
        this.bodyStorage      = bodyStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage       = carStorage;
    }


    @Override
    public void run() {
        Body body           = null;
        Motor motor         = null;
        Accessory accessory = null;
        try {
            body      = bodyStorage.pop();
            motor     = motorStorage.pop();
            accessory = accessoryStorage.pop();
        } catch (InterruptedException e) {
            // Retore prodcuts to storage
            if (body  != null) bodyStorage.put(body);
            if (motor != null) motorStorage.put(motor);
            Thread.currentThread().interrupt();
            return;
        }

        Car car = new Car(idGenerator.next(), body, motor, accessory);
        carStorage.put(car);
    }

}
