package ru.nsu.ccfit.gerasimov2.a.jcalc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.ParseException;

import java.util.logging.Level;
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
        LOGGER.info("========Start program========");
        try {
            CalculatorApp app = new CalculatorApp(args);
            
            app.run();
        } catch (ParseException e) {
            System.out.println("Failed to parse command line options: " + e);
            System.out.println("type --help for help options");
            LOGGER.log(Level.SEVERE, "Bad parsing", e);
        } catch (FileNotFoundException e) { 
            System.out.println("Error: " + e.getLocalizedMessage());
            LOGGER.log(Level.WARNING, "File not found: ", e);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Got unexpected throwable", e);
        }

        LOGGER.info("========End program========");
    }

}