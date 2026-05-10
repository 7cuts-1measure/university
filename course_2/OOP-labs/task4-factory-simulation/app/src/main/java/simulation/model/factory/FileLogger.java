package simulation.model.factory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slf4jansi.AnsiLogger;

public class FileLogger {
    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(FileLogger.class));

    private final PrintStream ps; 

    private boolean isClosed = false;

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

    public synchronized void close() {
        if (isClosed) return;
        ps.close();
        isClosed = true;
        log.info("log file closed");
    }

}
