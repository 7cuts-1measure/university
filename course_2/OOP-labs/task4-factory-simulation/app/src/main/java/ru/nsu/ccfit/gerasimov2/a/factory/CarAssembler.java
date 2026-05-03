package ru.nsu.ccfit.gerasimov2.a.factory;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Accessory;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Body;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Car;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;
import ru.nsu.ccfit.gerasimov2.a.threadpool.ThreadPool;

public class CarAssembler {
    private final int NUM_WORKERS = 20;

    Storage<Motor> motorStorage;
    Storage<Body> bodyStorage;
    Storage<Accessory> accessoryStorage;
    Storage<Car> carStorage;

    public CarAssembler(Storage<Motor> motorStorage, Storage<Body> bodyStorage,
            Storage<Accessory> accessoryStorage, Storage<Car> carStorage) {
        this.motorStorage = motorStorage;
        this.bodyStorage = bodyStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
    }

    private final ThreadPool workers = new ThreadPool(NUM_WORKERS);
    
    public void requestCarAssembly(int cnt) throws InterruptedException {
        for (int i = 0; i < cnt; i++) {
            Runnable task = new CarAssemblyTask(motorStorage, bodyStorage, accessoryStorage, carStorage);
            workers.submit(task);
        }
    }
}