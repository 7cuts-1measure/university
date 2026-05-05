package simulation.model;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.CarAssempler;
import simulation.model.factory.CarStorageController;
import simulation.model.factory.Dealer;
import simulation.model.factory.FileLogger;
import simulation.model.factory.IdGenerator;
import simulation.model.factory.Storage;
import simulation.model.factory.Supplier;
import simulation.model.factory.product.Accessory;
import simulation.model.factory.product.Body;
import simulation.model.factory.product.Car;
import simulation.model.factory.product.Motor;

public class FactoryModel extends Thread implements ViewModel{
    private final Logger log = LoggerFactory.getLogger(getClass());   

    private static final int DEFAULT_DEALER_PERFORMANCE = 2;
    private static final int DEFAULT_MOTOR_SUPPLIER_PERFROMANCE = 5;
    private static final int DEFAULT_BODY_SUPPLIER_PERFROMANCE = 10;
    private static final int DEFAULT_ACCESSORY_SUPPLIER_PERFORMANCE = 2;

    private final Storage<Motor> motorStorage;
    
    private final Storage<Accessory> accessoryStorage;
    
    private final Storage<Body> bodyStorage;
    
    private final Storage<Car> carStorage;

    private final FileLogger saleLogger;
    
    private final Supplier<Motor> motorSupplier;
    
    private final List<Supplier<Accessory>> accessorySuppliers;
    
    private final Supplier<Body> bodySupplier;
    
    private final List<Dealer> dealers;

    
    private final CarStorageController carStorageController;

    private final IdGenerator motorIdGenerator = new IdGenerator();
    private final IdGenerator bodyIdGenerator = new IdGenerator();
    private final IdGenerator accessoryIdGenerator = new IdGenerator();

    public FactoryModel() {
        this.saleLogger = new FileLogger();
        // Storages 
        motorStorage     = new Storage<Motor>(Config.getMotorStorageSize());
        accessoryStorage = new Storage<Accessory>(Config.getAccessoryStorageSize());
        bodyStorage      = new Storage<Body>(Config.getBodyStorageSize());
        carStorage       = new Storage<Car>(Config.getCarStorageSize());
            
        // Threads
        motorSupplier        = new Supplier<Motor>(DEFAULT_MOTOR_SUPPLIER_PERFROMANCE, motorIdGenerator, motorStorage, Motor::new);
        bodySupplier         = new Supplier<Body>(DEFAULT_BODY_SUPPLIER_PERFROMANCE, bodyIdGenerator, bodyStorage, Body::new);
        accessorySuppliers   = createAccessorySuppliers(accessoryIdGenerator, accessoryStorage);

        carStorageController = new CarStorageController(carStorage, new CarAssempler(bodyStorage, motorStorage, accessoryStorage, carStorage));
        dealers              = createDillers(carStorage, saleLogger);
    }

    @Override
    public void run() {
        try {
            startSimulation();
        } catch (InterruptedException e) {
            log.warn("Thread was interrupted while starting simulation");
        }
        log.info("Thread is interrupted");
    }


    private static List<Dealer> createDillers(Storage<Car> carStorage, FileLogger saleLogger) {
        List<Dealer> dealers = new ArrayList<>(Config.getThreadsDealers());
        final int cnt = Config.getThreadsDealers();
        for (int i = 0; i < cnt; i++) {
            dealers.add(new Dealer(i, DEFAULT_DEALER_PERFORMANCE, carStorage, saleLogger));
        }
        return dealers;
    }

    private void startSimulation() throws InterruptedException {
        log.info("Initializing factory threads");
        motorSupplier.start();
        bodySupplier.start();
        accessorySuppliers.forEach(Supplier<Accessory>::start);
        dealers.forEach(Dealer::start);
        carStorageController.start();

        log.info("Initialization complete");

        Thread.sleep(Duration.ofSeconds(30));

        interruptFactoryThreads();
        
        log.info("Factory simulation ended");
    }

    private void interruptFactoryThreads() {
        motorSupplier.interrupt();
        bodySupplier.interrupt();
        accessorySuppliers.forEach(Supplier::interrupt);
        dealers.forEach(Dealer::interrupt);
        carStorageController.interrupt();
    }

    private static List<Supplier<Accessory>> createAccessorySuppliers(IdGenerator gen, Storage<Accessory> accStorage) {
        List<Supplier<Accessory>> suppliers = new ArrayList<>();
        for (int i = 0; i < Config.getThreadsAccessorySuppliers(); i++) {
            suppliers.add(new Supplier<>(DEFAULT_ACCESSORY_SUPPLIER_PERFORMANCE, gen, accStorage, Accessory::new));
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
