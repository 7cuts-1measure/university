package ru.nsu.ccfit.gerasimov2.a.factory.supplier;

import java.time.Duration;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Product;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;

public class Supplier<T extends Product> implements Runnable {
    
    private int productsPerSecond;
    
    private final Storage<T> storage;

    private final Creator<T> factory;


    public Supplier(int productsPerSecond, Storage<T> storage, Creator<T> factory) {
        this.productsPerSecond = productsPerSecond;
        this.storage = storage;
        this.factory = factory;
    }


    private Duration sleepDuration() {
        final int millisInOneSecond = 1000;
        return Duration.ofMillis(millisInOneSecond / productsPerSecond);   
    }


    private void supplyStorage() throws InterruptedException {
        T product = factory.create();
        storage.put(product);
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                supplyStorage();
                Thread.sleep(sleepDuration());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Supplier " + Thread.currentThread().getName() + " is interrupted.");
            return;
        }
    }
}
