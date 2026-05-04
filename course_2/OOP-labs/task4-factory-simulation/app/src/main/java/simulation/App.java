package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.gui.SimulationWindow;
import simulation.model.Config;
import simulation.model.FactoryModel;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SetupLogger.setup();
        log.info("Main started");

        SimulationWindow window = new SimulationWindow();


        System.out.println("Config.getBodyStorageSize() = "           + Config.getBodyStorageSize());
        System.out.println("Config.getMotorStorageSize() = "          + Config.getMotorStorageSize());
        System.out.println("Config.getAccessoryStorageSize() = "      + Config.getAccessoryStorageSize());
        System.out.println("Config.getCarStorageSize() = "            + Config.getCarStorageSize());
        System.out.println("Config.getThreadsWorkers() = "            + Config.getThreadsWorkers());
        System.out.println("Config.getThreadsDealers() = "            + Config.getThreadsDealers());
        System.out.println("Config.getThreadsAccessorySuppliers() = " + Config.getThreadsAccessorySuppliers());


        FactoryModel model = new FactoryModel();
        model.startSimulation();
    }
}
