package simulation.model.factory;

import static java.lang.String.format;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.Config;
import simulation.model.factory.product.Car;
import slf4jansi.AnsiLogger;

public class Dealer extends Thread {
    private final static Logger log = AnsiLogger.of(LoggerFactory.getLogger(Dealer.class));

    private final Storage<Car> carStorage;

    private final AtomicBoolean isLogSale = new AtomicBoolean(Config.logSale());

    private final int number;

    private final FileLogger fileLogger;

    private AtomicInteger performance;

    public Dealer(int number, int performance, Storage<Car> carStorage, FileLogger fileLogger) {
        this.performance = new AtomicInteger(performance);
        this.carStorage = carStorage;
        this.number = number;
        this.fileLogger = fileLogger;
    }

    public int getPerformance() {
        return performance.get();
    }

    public void setPerformance(int performance) {
        this.performance.set(performance);
    }

    public boolean getIsLogSale() {
        return isLogSale.get();
    }

    public void setIsLogSale(boolean value) {
        isLogSale.set(value);
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                Car car = carStorage.take();
                sale(car);
                Thread.sleep(sleepDuration());
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
        String msg = logMessage(car);
        
        log.debug("Sale: " + msg);
        fileLogger.log(msg);
    }

    private String logMessage(Car car) {
        return format("Dealer %d: Auto %d: (Body %d: Motor %d: Accessory %d)",
                number, car.getId(),
                car.getBodyId(), car.getMotorId(), car.getAccessoryId());
    }

    private Duration sleepDuration() {
        final int millisInSecond = 1000;
        return Duration.ofMillis(millisInSecond / performance.get());
    }
}
