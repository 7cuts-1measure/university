package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulation.gui.SimulationWindow;
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

        FactoryModel model = new FactoryModel();
        
        model.startSimulation();
    }
}
