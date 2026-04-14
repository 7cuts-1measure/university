package ru.nsu.ccfit.gerasimov2.a.factory.storage;

import java.util.ArrayList;
import java.util.List;

public class Storage<T> {
    public final int capacity;
    private List<T> items = new ArrayList<>();

    /**
     * We use {@code lock} instead of {@code this} because we don't want to let other threads 
     * synchronize with {@code this} and interfere storage 
     * i.e. we don't want to allow other threads to write {@code synchonized(storage)}
     */
    private Object lock = new Object();
    
    public Storage(int capacity) {
        this.capacity = capacity;
    }
    

    public void put(T product) {
        if (product == null) return; 
        synchronized(lock) {
            // Use while (condition) istead if (condition) because
            // OS has spurious wakeup https://en.wikipedia.org/wiki/Spurious_wakeup
            // That means current thread can be awaken not from notify() or notifyall()
            // and storage still can be full
            while (items.size() >= capacity) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }                
            }
            items.add(product);
            assert items.size() <= capacity;
            lock.notifyAll(); 
        }
    }

    public T get() throws InterruptedException {
        synchronized(lock) {
            while (items.isEmpty()) {
                lock.wait();
            }
            T item = items.removeLast();
            lock.notifyAll();
            return item;
        }
    }


    public int size() {
        return items.size();
    }

}
