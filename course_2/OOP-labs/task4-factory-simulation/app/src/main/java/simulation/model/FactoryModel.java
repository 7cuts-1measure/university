package simulation.model;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.CarAssembler;
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
import slf4jansi.AnsiLogger;

public class FactoryModel extends Thread implements Model{
    private final Logger log = AnsiLogger.of(LoggerFactory.getLogger(getClass()));   

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

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private final CarStorageController carStorageController;
    private final CarAssembler carAssembler;

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

        carAssembler         = new CarAssembler(bodyStorage, motorStorage, accessoryStorage, carStorage);
        carStorageController = new CarStorageController(carStorage, carAssembler);
        dealers              = createDillers(carStorage, saleLogger);
    }

    @Override
    public void run() {
        try {
            simulate();
        } catch (InterruptedException e) {
            shutdown();
            log.warn("Thread was interrupted while starting simulation");
        }
        log.info("Thread is interrupted");
    }


    private static List<Dealer> createDillers(Storage<Car> carStorage, FileLogger saleLogger) {
        List<Dealer> dealers = new ArrayList<>(Config.getThreadsDealers());
        final int cnt = Config.getThreadsDealers();
        for (int i = 0; i < cnt; i++) {
            dealers.add(new Dealer(i, carStorage, saleLogger));
        }
        return dealers;
    }

    private synchronized void simulate() throws InterruptedException {
        if (isRunning.get() == false) {
            log.warn("terminated before run");
            return;
        }
        log.info("Initializing factory threads");
        motorSupplier.start();
        bodySupplier.start();
        accessorySuppliers.forEach(Supplier<Accessory>::start);
        dealers.forEach(Dealer::start);
        carStorageController.start();
        log.info("Initialization complete");        
    }

    public synchronized void shutdown() {
        log.info("Shutdown is called by " + Thread.currentThread().getName());
        boolean r = isRunning.getAndSet(false);
        if (r == false) {
            return;
        }

        
        carStorageController.interrupt();
        carAssembler.shutdown();

        motorSupplier.interrupt();
        bodySupplier.interrupt();
        accessorySuppliers.forEach(Supplier::interrupt);
        
        dealers.forEach(Dealer::interrupt);
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
        if (accessorySuppliers.size() == 0) {
            return 0;
        }
        return accessorySuppliers.get(0).getPerformance(); 
        
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
    public int getNumPendingTasks() {
        return carAssembler.getNumPendingTasks();
    }

    @Override
    public int getNumTotalCarsAssembled() {
        return carAssembler.totalCarsAssembled();
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
