package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slf4jansi.AnsiLogger;

import simulation.gui.SimulationWindow;
import simulation.model.FactoryModel;
import simulation.model.factory.FileLoggerException;

public class App {
    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(App.class));

    public static void main(String[] args) {        
        log.info("Main started");

        FactoryModel model = null; 
        try {
            model = new FactoryModel();
            SimulationWindow window = new SimulationWindow(model);
            window.show();
            model.start();
        } catch (FileLoggerException e) {
            log.error("Cannot fix " + e + ". Terminating...");
            return;
        }
    }
}
