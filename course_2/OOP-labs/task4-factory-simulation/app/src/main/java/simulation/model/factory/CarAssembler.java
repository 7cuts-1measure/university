package simulation.model.factory;

import static java.util.stream.IntStream.range;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Config;
import simulation.model.factory.product.Accessory;
import simulation.model.factory.product.Body;
import simulation.model.factory.product.Car;
import simulation.model.factory.product.Motor;
import slf4jansi.AnsiLogger;

public class CarAssembler {
    private final IdGenerator idGenerator = new IdGenerator();

    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(CarAssembler.class));

    private final Duration ASSEMBLY_EMULATING_TIME = Duration.ofMillis(20);

    private final AtomicInteger pendingTasks;
    private final AtomicInteger total = new AtomicInteger();

    private final Runnable CAR_ASSMEBLY_TASK = new Runnable() {
        @Override
        public void run() {
            Body body;
            try {
                body = bodyStorage.take();
                Motor motor = motorStorage.take();
                Accessory accessory = accessoryStorage.take();
                Car car = new Car(idGenerator.next(), body, motor, accessory);
                
                // emulating assemblin0g
                Thread.sleep(ASSEMBLY_EMULATING_TIME);
                
                pendingTasks.decrementAndGet();
                carStorage.put(car);
            } catch (InterruptedException e) {
                log.info("Thread was iterrupted during running car assemnly task");
            }

        }
    };

    private final Executor workers;

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
        workers               = Executors.newFixedThreadPool(Config.getThreadsWorkers());
    }

    public int getNumPendingTasks() {
        return pendingTasks.get();
    }

    public void requestAssembly(int num_requests) {
        pendingTasks.set(num_requests);
        total.addAndGet(num_requests);
        range(0, num_requests)
                .forEach(i -> workers.execute(CAR_ASSMEBLY_TASK));
    }

    public int totalCarsAssembled() {
        return total.get();
    }

}
