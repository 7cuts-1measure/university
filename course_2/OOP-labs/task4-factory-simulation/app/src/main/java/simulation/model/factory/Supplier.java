package simulation.model.factory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.product.Product;
import slf4jansi.AnsiLogger;

public class Supplier<T extends Product> extends Thread {

    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(Supplier.class));

    private final IdGenerator idGenerator;

    private final Creator<T> creator;

    private final Storage<T> storage;

    // details per second
    private final AtomicInteger performance;

    public Supplier(int perfromance, IdGenerator idGenerator, Storage<T> storage, Creator<T> creator) {
        this.idGenerator = idGenerator;
        this.performance = new AtomicInteger(perfromance);
        this.storage = storage;
        this.creator = creator;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                supplyStorage();
                sleep(sleepDuration());
            } catch (InterruptedException e) {
                break;
            }
        }
        log.info("Thread is interrupted");
    }

    private void supplyStorage() throws InterruptedException {
        T product = creator.newProduct(idGenerator.next());
        log.debug("Created " + product.getName() + " <ID:" + product.getId() + ">");
        storage.put(product);
    }

    public void setPerformance(int performance) {
        this.performance.set(performance);
    }

    private Duration sleepDuration() {
        final int millisInSecond = 1000;
        return Duration.ofMillis(millisInSecond / performance.get());
    }

    public int getPerformance() {
        return performance.get();
    }
}
