package simulation.model.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.model.factory.product.Car;

public class Dealer extends Thread {
    private final static Logger log = LoggerFactory.getLogger(Dealer.class);
    
    private final Storage<Car> carStorage;

    public Dealer(Storage<Car> carStorage) {
        this.carStorage = carStorage;
    }

    @Override
    public void run() {
        //  TODO
        log.error("Dealer thread is not implemented yet");
    }
}
