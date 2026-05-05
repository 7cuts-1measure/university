package simulation.model;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.ControllerModel;
import simulation.ViewModel;
import simulation.model.factory.CarAssempler;
import simulation.model.factory.CarStorageController;
import simulation.model.factory.Dealer;
import simulation.model.factory.FileLogger;
import simulation.model.factory.FileLoggerException;
import simulation.model.factory.IdGenerator;
import simulation.model.factory.Storage;
import simulation.model.factory.Supplier;
import simulation.model.factory.product.Accessory;
import simulation.model.factory.product.Body;
import simulation.model.factory.product.Car;
import simulation.model.factory.product.Motor;

public class FactoryModel extends Thread implements ViewModel, ControllerModel{
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

    public FactoryModel() throws FileLoggerException {
        this.saleLogger = new FileLogger(Config.logFileName());
        // Storages 
        motorStorage     = new Storage<Motor>(Config.getMotorStorageCap());
        accessoryStorage = new Storage<Accessory>(Config.getAccessoryStorageCap());
        bodyStorage      = new Storage<Body>(Config.getBodyStorageCap());
        carStorage       = new Storage<Car>(Config.getCarStorageCap());
            
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
        return motorSupplier.getPerformance();
    }

    @Override
    public int getBodySupplierPerformance() {
        return bodySupplier.getPerformance();
    }

    @Override
    public int getAccessorySupplierPerformance() {
        return accessorySuppliers.get(0).getPerformance(); // FIXME: assert somewhere that we have at least one accessory supplier 
    }

    @Override
    public int getNumAccessorySuppliers() {
        return accessorySuppliers.size();
    }

    @Override
    public int getMotorStorageSize() throws InterruptedException {
        return motorStorage.size();
    }

    @Override
    public int getMotorStorageCap() {
        return Config.getMotorStorageCap();
    }

    @Override
    public int getBodySorageSize() throws InterruptedException {
        return bodyStorage.size();
    }

    @Override
    public int getBodyStorageCap() {
        return Config.getBodyStorageCap();
    }

    @Override
    public int getAccessoryStorageSize() throws InterruptedException {
        return accessoryStorage.size();
    }

    @Override
    public int getAccessoryStorageCap() {
        return Config.getAccessoryStorageCap();
    }

    @Override
    public int getCarStorageSize() throws InterruptedException {
        return carStorage.size();
    }

    @Override
    public int getCarStorageCap() {
        return Config.getCarStorageCap();
    }

    @Override
    public int getNumActiveWorkers() {
        return -1;
    }

    @Override
    public int getNumTotalWorkets() {
        return Config.getThreadsWorkers();
    }

    @Override
    public void setMotorSupplierPerformance(int performance) {
        motorSupplier.setPerformance(performance);
    }

    @Override
    public void setBodySupplierPerformance(int performance) {
        bodySupplier.setPerformance(performance);
    }

    @Override
    public void setAccessorySupplierPerformance(int performance) {
        accessorySuppliers
            .forEach(sup -> sup.setPerformance(performance));
    }
}
