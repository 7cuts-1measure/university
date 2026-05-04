package simulation.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactoryModel {
    private final Logger log = LoggerFactory.getLogger(getClass());   
    private final Config config;

    private final ThreadPoolExecutor workers;

    public FactoryModel(Config config) {
        this.config = null;
        workers = null;
        // TODO...
    }

    public void startSimulation() {
        log.info("Factory simulation started");



        log.info("Factory simulation ended");
    }
}
