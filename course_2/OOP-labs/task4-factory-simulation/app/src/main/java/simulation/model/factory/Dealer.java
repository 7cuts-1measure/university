package simulation.model.factory;

import static java.lang.String.format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Config;
import simulation.model.factory.product.Car;
import slf4jansi.AnsiLogger;

public class Dealer extends Thread {
    private final static Logger log = AnsiLogger.of(LoggerFactory.getLogger(Dealer.class));

    private final Storage<Car> carStorage;

    private final int number;

    private final FileLogger fileLogger;


    public Dealer(int number, Storage<Car> carStorage, FileLogger fileLogger) {
        this.carStorage = carStorage;
        this.number = number;
        this.fileLogger = fileLogger;
    }
    
    @Override
    public void run() {
        while (!interrupted()) {
            try {
                Car car = carStorage.take();
                sale(car);
            } catch (InterruptedException e) {
                fileLogger.close();
                interrupt();
            }
        }
        log.info("Dealer is interrupted");
    }

    private void sale(Car car) {
        logSale(car);
    }

    private void logSale(Car car) {
        if (Config.logSale()) {
            String msg = logMessage(car);
            fileLogger.log(msg);
        } 
    }

    private String logMessage(Car car) {
        return format("Dealer %d: Auto %d: (Body %d: Motor %d: Accessory %d)",
                number, car.getId(),
                car.getBodyId(), car.getMotorId(), car.getAccessoryId());
    }
}
