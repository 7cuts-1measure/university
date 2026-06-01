package simulation.model.factory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread safety generator!!!
 */
public class IdGenerator {

    private AtomicInteger id = new AtomicInteger();

    public int next() {
        return id.getAndIncrement();
    }

}
