package simulation.model.factory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.product.Product;

/**
 * Thread-safety storage
 */
public class Storage<T extends Product> {
    private static final Logger log = LoggerFactory.getLogger(Storage.class);
    private final BlockingQueue<T> storage; // TODO: implement synchonized list by myself

    public Storage(int capacity) {
        storage = new LinkedBlockingQueue<>(capacity);
    }

    public void put(T product) throws InterruptedException {
        if (product == null) {
            log.warn("Ignored null product");
            return;
        }
        storage.put(product);
    }

}
