package simulation.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactoryModel {
    private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());   

    public FactoryModel() {
    }

    public void startSimulation() {
        log.info("Factory simulation started");
        log.info("Factory simulation ended");
    }
}
