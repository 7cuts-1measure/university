package simulation.model.factory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safety storage
 */
public class Storage<T> {

    private final BlockingQueue<T> storage;

    public Storage(int capacity) {
        storage = new LinkedBlockingQueue<>(capacity);
    }

    public void put(T product) throws InterruptedException {
        storage.put(product);
    }

}
