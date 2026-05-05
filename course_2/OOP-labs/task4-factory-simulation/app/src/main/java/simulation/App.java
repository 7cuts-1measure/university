package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.gui.SimulationWindow;
import simulation.model.Config;
import simulation.model.FactoryModel;
import simulation.model.factory.FileLoggerException;
import slf4jansi.AnsiLogger;

public class App {
    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(App.class));

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        
        log.info("Main started");

        //SimulationWindow window = new SimulationWindow();


        System.out.println("Config.getBodyStorageSize() = "           + Config.getBodyStorageSize());
        System.out.println("Config.getMotorStorageSize() = "          + Config.getMotorStorageSize());
        System.out.println("Config.getAccessoryStorageSize() = "      + Config.getAccessoryStorageSize());
        System.out.println("Config.getCarStorageSize() = "            + Config.getCarStorageSize());
        System.out.println("Config.getThreadsWorkers() = "            + Config.getThreadsWorkers());
        System.out.println("Config.getThreadsDealers() = "            + Config.getThreadsDealers());
        System.out.println("Config.getThreadsAccessorySuppliers() = " + Config.getThreadsAccessorySuppliers());


        FactoryModel model;
        try {
            model = new FactoryModel();
        } catch (FileLoggerException e) {
            log.error("Cannot fix " + e + ". Terminating...");
            return;
        }
        model.start();
    }
}
