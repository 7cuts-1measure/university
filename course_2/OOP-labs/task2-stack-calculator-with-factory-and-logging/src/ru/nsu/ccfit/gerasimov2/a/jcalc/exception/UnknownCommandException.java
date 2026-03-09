package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

public class UnknownCommandException extends FactoryException {

    public UnknownCommandException(String cmdName) {
        super("Unknown command: " + cmdName);
    }

    public UnknownCommandException(String cmdName, Throwable cause) {
        super("Unknown command: " + cmdName, cause);
    }

}
