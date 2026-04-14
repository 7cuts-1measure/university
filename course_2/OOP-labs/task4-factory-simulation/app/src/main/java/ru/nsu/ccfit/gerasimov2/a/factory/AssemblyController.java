package ru.nsu.ccfit.gerasimov2.a.factory;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Car;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.threadpool.ThreadPool;

public class AssemblyController {
    Storage<Motor> motorStorage;
    Storage<Body> bodyStorage;
    Storage<Accessory> accessoryStorage;
    Storage<Car> carStorage;

    public AssemblyController(Storage<Motor> motorStorage, Storage<Body> bodyStorage,
            Storage<Accessory> accessoryStorage, Storage<Car> carStorage) {
        this.motorStorage = motorStorage;
        this.bodyStorage = bodyStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
    }

    private final ThreadPool workers = new ThreadPool();
    
    public void requestCarAssembly() {
        Runnable task = new CarAssemblyTask(motorStorage, bodyStorage, accessoryStorage, carStorage);
        workers.submit(task);
    }
}
