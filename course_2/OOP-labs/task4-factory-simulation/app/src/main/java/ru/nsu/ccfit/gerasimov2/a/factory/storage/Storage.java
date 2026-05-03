package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import java.util.ArrayList;
import java.util.List;

public class Storage<T> {
    public final int capacity;
    private List<T> storage = new ArrayList<>();

    /**
     * We use {@code lock} instead of {@code this} because we don't want to let other threads 
     * synchronize with {@code this} and interfere storage 
     * i.e. we don't want to allow other threads to write {@code synchonized(storage)}
     */
    
    public Storage(int capacity) {
        this.capacity = capacity;
    }
    

    public void put(T product) {
        if (product == null) return; 
        synchronized(storage) {
            // Use while (condition) istead if (condition) because
            // OS has spurious wakeup https://en.wikipedia.org/wiki/Spurious_wakeup
            // That means current thread can be awaken not from notify() or notifyall()
            // and storage still can be full
            while (storage.size() >= capacity) {
                try {
                    storage.wait(); // wait until another thread call pop() and removes product
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }                
            }
            storage.add(product);
            assert storage.size() <= capacity;
            storage.notifyAll(); 
        }
    }

    public T pop() throws InterruptedException {
        synchronized(storage) {
            while (storage.isEmpty()) {
                storage.wait(); // wait until another thread call put() and fill the storage
            }
            T item = storage.removeLast();
            storage.notifyAll();
            return item;
        }
    }


    public int size() {
        return storage.size();
    }

}
