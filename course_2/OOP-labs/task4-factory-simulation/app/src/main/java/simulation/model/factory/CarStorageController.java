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

        checkStorageSize();
        while (!interrupted()) {
            try {
                carStorage.waitTake();
            } catch (InterruptedException e) {
                break;
            }
            checkStorageSize();
        }

        log.info("Thread terminated");
    }

    private void checkStorageSize() {
        try {
            boolean assemblyRequestDone = carAssembler.getNumPendingTasks() == 0;
            boolean criticalSize = carStorage.size() < CRITICAL_STORAGE_SIZE;
            if (assemblyRequestDone && criticalSize) {
                log.debug("{!warn} request car assembly");
                requestAssembly();
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }

    private void requestAssembly() {
        carAssembler.requestAssembly(NUM_REQUESTS);
    }
}
