package simulation.model.factory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Supplier<T> extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final IdGenerator idGenerator = new IdGenerator();
    private final Creator<T> creator;
    private final Storage<T> storage;

    private AtomicInteger performance;
    
    public void setPerformance(int performance) {
        this.performance.set(performance);
    }

    public Supplier(int performance, Storage<T> storage, Creator<T> creator) {
        this.performance = new AtomicInteger();
        this.performance.set(performance);
        this.storage = storage;
        this.creator = creator;
    }

    private void supplyStorage() throws InterruptedException {
        T product = creator.newProduct(idGenerator.next());
        storage.put(product);
    }

    @Override
    public void run() {
        try {
            while (true) {
                sleep(sleepDuration());
                supplyStorage();
            }
        } catch (InterruptedException e) {
            log.info("Thread was interrupted");
            return;
        }
    }

    private Duration sleepDuration() {
        final int millisInSecond = 1000;
        return Duration.ofMillis(millisInSecond / performance.get());
    }
}
