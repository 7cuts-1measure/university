package common;

import java.io.PrintStream;

public class Log {
    private final LogLevel level;
    private final PrintStream out;
    
    public Log(PrintStream out, LogLevel level) {
        this.level = level;
        this.out = out;
    }

    public Log(LogLevel level) {
        this.level = level;
        out = System.out;
    }

    public void info(String msg) {
        if (level == LogLevel.DEBUG || level == LogLevel.INFO) 
            out.println("INFO: " + msg);
    }

    public void debug(String msg) {
        if (level == LogLevel.DEBUG)
            out.println("DEBUG: " + msg);
    }

    public void err(String msg) {
        out.println("ERROR: " + msg);
    }

    public void warn(String msg) {
        if (level != LogLevel.ERROR);
            out.println("WARN: " + msg);
    }
}
