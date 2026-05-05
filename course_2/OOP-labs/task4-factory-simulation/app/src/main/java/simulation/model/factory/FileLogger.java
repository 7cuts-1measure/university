package simulation.model.factory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileLogger {

    private final PrintStream ps; 
    public FileLogger(String fileName) throws FileLoggerException {
        try {
            ps = new PrintStream(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            throw new FileLoggerException();
        }
    }

    public void log(String message) {
        ps.println(message);        
    }

    public void close() {
        ps.close();
    }

}
