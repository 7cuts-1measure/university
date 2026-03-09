package test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;

public class BaseTest {
    @BeforeClass
    public static void disableLogging() {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);
        for (Handler h : logger.getHandlers()) {
            h.setLevel(Level.OFF);
            logger.removeHandler(h);
        }
    }
}