package simulation.model.factory;


import java.util.ArrayList;
import java.util.List;

import simulation.model.factory.product.Product;


public class Storage<T extends Product> {
    public final int capacity;
    private List<T> storage = new ArrayList<>();

    private final Object storageLock = new Object();

    public Storage(int capacity) {
        this.capacity = capacity;
    }
    

    public void put(T product) throws InterruptedException {
        if (product == null) return; 
        synchronized(storageLock) {
            // Use while (condition) istead if (condition) because
            // OS has spurious wakeup https://en.wikipedia.org/wiki/Spurious_wakeup
            // That means current thread can be awaken not from notify() or notifyall()
            // and storage still can be full
            while (storage.size() == capacity) {
                storageLock.wait();
            }
            storage.add(product);
            assert storage.size() <= capacity;
            storageLock.notifyAll(); 
        }
    }

    public T take() throws InterruptedException {
        synchronized(storageLock) {
            while (storage.isEmpty()) {
                storageLock.wait();
            }
            T item = storage.removeLast();
            storageLock.notifyAll();
            return item;
        }
    }

    public Object getStorageLock() {
        return storageLock;
    }

    public int size() throws InterruptedException {
        synchronized(storageLock) {
            return storage.size();
        }
    }
}