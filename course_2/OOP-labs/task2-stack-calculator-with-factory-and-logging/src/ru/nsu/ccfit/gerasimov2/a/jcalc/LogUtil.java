package ru.nsu.ccfit.gerasimov2.a.jcalc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtil {
    static boolean isFirstTime = true;
    public static Logger getLogger(String name) {
        if (!isFirstTime) {
            return Logger.getLogger(name);
        }

        isFirstTime = false;
        try (InputStream ins = CalculatorApp.class.getResourceAsStream("log.properties")){
            LogManager.getLogManager().readConfiguration(ins);
            return Logger.getLogger(name);
        }catch (IOException e){
            e.printStackTrace();
            Logger global = Logger.getGlobal(); 
            global.warning("Cannot create named logger because " + e.getLocalizedMessage() + " - Use global logger");
            return global;
        }
    }
}
