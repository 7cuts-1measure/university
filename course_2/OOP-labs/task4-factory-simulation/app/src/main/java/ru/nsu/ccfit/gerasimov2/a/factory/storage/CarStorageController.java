package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import ru.nsu.ccfit.gerasimov2.a.factory.CarAssembler;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Car;

public class CarStorageController extends Thread {
    private final Storage<Car> carStorage;
    private final CarAssembler carAssembler;
    private final int numCarsForRequest;
    
    public CarStorageController(Storage<Car> carStorage, CarAssembler carAssembler, int numCarsForRequest) {
        this.carStorage = carStorage;
        this.carAssembler = carAssembler;
        this.numCarsForRequest = numCarsForRequest;
    }

    @Override
    public void run() {
        Object carStorageLock = carStorage.getStorageLock();
        try {
            while (true) {
                synchronized(carStorageLock) {
                    if (carStorage.isEmpty()) {
                        carAssembler.requestCarAssembly(numCarsForRequest);
                    }
                    carStorageLock.wait();
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Factory controller " + Thread.currentThread().getName() + "is interrupted");
            Thread.currentThread().interrupt();
            return;
        }
    }
       
}
