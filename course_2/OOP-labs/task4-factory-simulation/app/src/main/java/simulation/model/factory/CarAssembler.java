package simulation.model.factory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Config;
import simulation.model.factory.product.Accessory;
import simulation.model.factory.product.Body;
import simulation.model.factory.product.Car;
import simulation.model.factory.product.Motor;
import simulation.model.threadpool.TaskRunner;
import slf4jansi.AnsiLogger;

public class CarAssembler {
    private final IdGenerator idGenerator = new IdGenerator();

    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(CarAssembler.class));

    private final AtomicInteger pendingTasks;
    private final AtomicInteger total = new AtomicInteger();

    private final Runnable CAR_ASSMEBLY_TASK = new Runnable() {
        @Override
        public void run() {
            try {
                Body body = bodyStorage.take();
                Motor motor = motorStorage.take();
                Accessory accessory = accessoryStorage.take();
                Car car = new Car(idGenerator.next(), body, motor, accessory);
                    
                pendingTasks.decrementAndGet();
                total.incrementAndGet();
                carStorage.put(car);
            } catch (InterruptedException e) {
                log.info("Worker is iterrupted");
                Thread.currentThread().interrupt(); // IMPORTANT: restore interrupt flag!!
            }

        }
    };

    private final TaskRunner workers;

    private final Storage<Body> bodyStorage;

    private final Storage<Motor> motorStorage;

    private final Storage<Accessory> accessoryStorage;

    private final Storage<Car> carStorage;

    public CarAssembler(Storage<Body> bodyStorage, Storage<Motor> motorStorage, Storage<Accessory> accessoryStorage,
            Storage<Car> carStorage) {
        this.bodyStorage      = bodyStorage;
        this.motorStorage     = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage       = carStorage;
        pendingTasks          = new AtomicInteger(0);
        workers               = new TaskRunner(Config.getThreadsWorkers());
    }

    public int getNumPendingTasks() {
        return pendingTasks.get();
    }

    public void shutdown() {
        workers.shutdown();
    }

    public void requestAssembly(int num_requests) throws InterruptedException {
        pendingTasks.set(num_requests);
        for (int i = 0; i < num_requests; i++) {
            workers.submit(CAR_ASSMEBLY_TASK);
        }
    }

    public int totalCarsAssembled() {
        return total.get();
    }

}
