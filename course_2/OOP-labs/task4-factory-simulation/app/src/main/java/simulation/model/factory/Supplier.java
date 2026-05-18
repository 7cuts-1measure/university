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
                waitForNextCycle();
            } catch (InterruptedException e) {
                break;
            }
        }
        log.info("Thread is interrupted");
    }

    private synchronized void waitForNextCycle() throws InterruptedException {
        long timeout = sleepDuration().toMillis();
        if (timeout > 0)
            wait(timeout);
    }

    private void supplyStorage() throws InterruptedException {
        T product = creator.newProduct(idGenerator.next());
        storage.put(product);
    }

    public void setPerformance(int performance) {
        this.performance.set(performance);
        synchronized (this) {
            log.info("set perfromance to " + performance);
            notify();
        }
    }
    
    private Duration sleepDuration() {
        final long nanosInSecond = 1_000_000_000L;
        int perf = performance.get();
        
        if (perf == 0) {
            return Duration.ofDays(9999999999L);
        }
        
        long nanos = nanosInSecond / perf;
        return Duration.ofNanos(nanos);
    }

    public int getPerformance() {
        return performance.get();
    }
}
