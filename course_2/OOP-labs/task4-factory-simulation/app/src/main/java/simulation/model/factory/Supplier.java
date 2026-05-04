package simulation.model.factory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Supplier<T> extends Thread {
    private static final int DEFAULT_PERFORMANCE = 5;

    private static final Logger log = LoggerFactory.getLogger(Supplier.class);

    private final IdGenerator idGenerator = new IdGenerator();

    private final Creator<T> creator;

    private final Storage<T> storage;

    // details per second
    private final AtomicInteger performance = new AtomicInteger(DEFAULT_PERFORMANCE);

    public Supplier(Storage<T> storage, Creator<T> creator) {
        this.performance.set(DEFAULT_PERFORMANCE);
        this.storage = storage;
        this.creator = creator;
    }

    @Override
    public void run() {
        try {
            while (true) {
                supplyStorage();
                sleep(sleepDuration());
            }
        } catch (InterruptedException e) {
            log.info("Thread was interrupted");
            return;
        }
    }

    private void supplyStorage() throws InterruptedException {
        if (log.isDebugEnabled()) log.debug(getName());
        T product = creator.newProduct(idGenerator.next());
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
