package simulation.model;

import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.CarStorageController;
import simulation.model.factory.Dealer;
import simulation.model.factory.IdGenerator;
import simulation.model.factory.Storage;
import simulation.model.factory.Supplier;
import simulation.model.factory.product.Accessory;
import simulation.model.factory.product.Body;
import simulation.model.factory.product.Car;
import simulation.model.factory.product.Motor;

public class FactoryModel extends Thread implements ViewModel{
    private final Logger log = LoggerFactory.getLogger(getClass());   


    private final Storage<Motor> motorStorage;
    
    private final Storage<Accessory> accessoryStorage;
    
    private final Storage<Body> bodyStorage;
    
    private final Storage<Car> carStorage;

    
    private final Supplier<Motor> motorSupplier;
    
    private final List<Supplier<Accessory>> accessorySuppliers;
    
    private final Supplier<Body> bodySupplier;
    
    private final List<Dealer> dealers;

    
    private final CarStorageController carStorageController;

    private final IdGenerator idGenerator = new IdGenerator();

    private final ExecutorService workers;

    public FactoryModel() {
        // Storages 
        motorStorage     = new Storage<Motor>(Config.getMotorStorageSize());
        accessoryStorage = new Storage<Accessory>(Config.getAccessoryStorageSize());
        bodyStorage      = new Storage<Body>(Config.getBodyStorageSize());
        carStorage       = new Storage<Car>(Config.getCarStorageSize());
        
        // ThreadPool for assembling cars
        workers = Executors.newFixedThreadPool(Config.getThreadsWorkers());
    
        // Threads
        motorSupplier        = new Supplier<Motor>(idGenerator, motorStorage, Motor::new);
        bodySupplier         = new Supplier<Body>(idGenerator, bodyStorage, Body::new);
        accessorySuppliers   = createAccessorySuppliers(idGenerator, accessoryStorage);
        carStorageController = new CarStorageController();
        dealers              = createDillers(carStorage);
    }

    @Override
    public void run() {
        startSimulation();
    }


    private static List<Dealer> createDillers(Storage<Car> carStorage) {
        List<Dealer> dealers = new ArrayList<>(Config.getThreadsDealers());
        final int cnt = Config.getThreadsDealers();
        for (int i = 0; i < cnt; i++) {
            dealers.add(new Dealer(carStorage));
        }
        return dealers;
    }

    private void startSimulation() {
        log.info("Initializing factory threads");
        motorSupplier.start();
        bodySupplier.start();
        accessorySuppliers.forEach(Supplier<Accessory>::start);
        carStorageController.start();

        range(0, Config.getThreadsDealers())
                .forEach(i -> new Dealer(carStorage).start());

        log.info("Initialization complete");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {return;}

        interruptFactoryThreads();
        
        log.info("Factory simulation ended");
    }

    private void interruptFactoryThreads() {
        motorSupplier.interrupt();
        bodySupplier.interrupt();
        accessorySuppliers.forEach(Supplier::interrupt);
        dealers.forEach(Dealer::start);
        carStorageController.interrupt();
    }

    private static List<Supplier<Accessory>> createAccessorySuppliers(IdGenerator gen, Storage<Accessory> accStorage) {
        List<Supplier<Accessory>> suppliers = new ArrayList<>();
        for (int i = 0; i < Config.getThreadsAccessorySuppliers(); i++) {
            suppliers.add(new Supplier<>(gen, accStorage, Accessory::new));
        }
        return suppliers;
    }

    @Override
    public int getMotorSupplierPerformance() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMotorSupplierPerformance'");
    }

    @Override
    public int getBodySupplierPerformance() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBodySupplierPerformance'");
    }

    @Override
    public int getAccessorySupplierPerformance() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAccessorySupplierPerformance'");
    }

    @Override
    public int getNumAccessorySuppliers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNumAccessorySuppliers'");
    }

    @Override
    public int getMotorStorageSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMotorStorageSize'");
    }

    @Override
    public int getMotorStorageCap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMotorStorageCap'");
    }

    @Override
    public int getBodySorageSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBodySorageSize'");
    }

    @Override
    public int getBodyStorageCap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBodyStorageCap'");
    }

    @Override
    public int getAccessoryStorageSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAccessoryStorageSize'");
    }

    @Override
    public int getAccessoryStorageCap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAccessoryStorageCap'");
    }

    @Override
    public int getCarStorageSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCarStorageSize'");
    }

    @Override
    public int getCarStorageCap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCarStorageCap'");
    }

    @Override
    public int getNumActiveWorkers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNumActiveWorkers'");
    }

    @Override
    public int getNumTotalWorkets() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNumTotalWorkets'");
    }
}
