package simulation.model.factory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safety storage
 */
public class Storage<T> {

    BlockingQueue<T> storage = new LinkedBlockingQueue<>();

    public void put(T product) throws InterruptedException {
        storage.put(product);
    }

}
