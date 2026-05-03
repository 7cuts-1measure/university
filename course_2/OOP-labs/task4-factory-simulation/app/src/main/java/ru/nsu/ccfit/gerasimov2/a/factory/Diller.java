package ru.nsu.ccfit.gerasimov2.a.factory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import ru.nsu.ccfit.gerasimov2.a.factory.product.Car;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;

public class Diller extends Thread {
    private final Storage<Car> carStorage;
    
    private AtomicInteger numSellingsPerSecond;

    public void setNumSellingsPerSecond(int num) {
        numSellingsPerSecond.set(num);
    }

    private Duration sleepDuration() {
        final int millisInSec = 1000;
        return Duration.ofMillis(millisInSec / numSellingsPerSecond.get());
    }

    public Diller(Storage<Car> catStorage, int carsSellingPerSecond) {
        this.numSellingsPerSecond = new AtomicInteger(carsSellingPerSecond);
        this.carStorage = catStorage;
    }

    @Override
    public void run() {
        try {
            while (true) {
                carStorage.pop();
                sleep(sleepDuration());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }   
    }
}
