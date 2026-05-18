package simulation.model.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Storage<T> {
    public final int capacity;
    private List<T> storage = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public void put(T product) throws InterruptedException {
        if (product == null)
            return;
        lock.lockInterruptibly();
        try {
            while (storage.size() == capacity) {
                notFull.await();
            }
            storage.add(product);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (storage.size() == 0) {
                notEmpty.await();
            }
            T product = storage.removeLast();
            notFull.signal();
            return product;
        } finally {
            lock.unlock();
        }
    }

    public int size() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            return storage.size();
        } finally {
            lock.unlock();
        }
    }

    public void waitTake() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            notFull.await();
        } finally {
            lock.unlock();
        }
    }
}