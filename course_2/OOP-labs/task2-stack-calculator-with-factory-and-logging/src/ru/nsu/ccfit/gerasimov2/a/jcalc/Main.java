package ru.nsu.ccfit.gerasimov2.a.jcalc;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    static Logger LOGGER;
    static {
        try (InputStream ins = Main.class.getResourceAsStream("log.properties")){
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(Main.class.getSimpleName());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        CalculatorApp app = new CalculatorApp(args);    
        app.run();   
    }

}