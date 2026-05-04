package simulation.model.factory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.product.Product;

public class Supplier<T extends Product> extends Thread {
    private static final int DEFAULT_PERFORMANCE = 5;

    private static final Logger log = LoggerFactory.getLogger(Supplier.class);

    private final IdGenerator idGenerator;

    private final Creator<T> creator;

    private final Storage<T> storage;

    // details per second
    private final AtomicInteger performance = new AtomicInteger(DEFAULT_PERFORMANCE);

    public Supplier(IdGenerator idGenerator, Storage<T> storage, Creator<T> creator) {
        this.idGenerator = idGenerator;
        this.performance.set(DEFAULT_PERFORMANCE);
        this.storage = storage;
        this.creator = creator;
    }

    @Override
    public void run() {
        try {
            while (Thread.interrupted()) {
                supplyStorage();
                sleep(sleepDuration());
            }
        } catch (InterruptedException e) {} 
        finally {
            log.info("Thread was interrupted");
        }
        return;
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
