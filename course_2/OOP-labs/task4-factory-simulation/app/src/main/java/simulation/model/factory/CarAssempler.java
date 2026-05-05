package simulation.model.factory;

import static java.util.stream.IntStream.range;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Config;
import simulation.model.factory.product.Accessory;
import simulation.model.factory.product.Body;
import simulation.model.factory.product.Car;
import simulation.model.factory.product.Motor;
import slf4jansi.AnsiLogger;


public class CarAssempler {
    private final IdGenerator idGenerator = new IdGenerator();

    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(CarAssempler.class));

    private final Runnable CAR_ASSMEBLY_TASK = new Runnable() {
        
        @Override
        public void run() {
            Body body;
            try {
                body                = bodyStorage.take();
                Motor motor         = motorStorage.take();
                Accessory accessory = accessoryStorage.take();
                Car car = new Car(idGenerator.next(), body, motor, accessory);
                log.debug("Assemblied car " + car.getId());
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

    public CarAssempler(Storage<Body> bodyStorage, Storage<Motor> motorStorage, Storage<Accessory> accessoryStorage, Storage<Car> carStorage) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage; 
        workers = Executors.newFixedThreadPool(Config.getThreadsWorkers());
    } 

    
    public void requestAssembly(int num_requests) {
        range(0, num_requests)
            .forEach(i -> workers.execute(CAR_ASSMEBLY_TASK));
    }

}
