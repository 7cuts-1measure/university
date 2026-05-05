package simulation.model.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Config;
import simulation.model.factory.product.Car;
import slf4jansi.AnsiLogger;

public class CarStorageController extends Thread {
    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(CarStorageController.class));
    private static final int CRITICAL_STORAGE_SIZE = Config.getCriticalCarStorageSize();
    private static final int NUM_REQUESTS = Config.numRequests();
    
    private final CarAssempler carAssembler;
    private final Storage<Car> carStorage;
    
    public CarStorageController(Storage<Car> carStorage, CarAssempler carAssempler) {
        this.carAssembler = carAssempler;
        this.carStorage = carStorage;
    }

    @Override
    public void run() {
        log.info("Thread started");

        Object carStorageLock = carStorage.getStorageLock();
        while (!interrupted()) {
            try {
                synchronized(carStorageLock) {
                    log.debug("carStorageController woke up");
                    if (carStorage.size() < CRITICAL_STORAGE_SIZE) {
                        log.warn("Car storage size: " + carStorage.size() + ". Requesting to assembly " + NUM_REQUESTS + " cars");
                        requestAssembly();
                    }
                    carStorageLock.wait();
                }
            } catch (InterruptedException e) {
                interrupt();
            }
        }


        log.info("Thread terminated");
    }

    private void requestAssembly() {
        carAssembler.requestAssembly(NUM_REQUESTS);
    }
}
