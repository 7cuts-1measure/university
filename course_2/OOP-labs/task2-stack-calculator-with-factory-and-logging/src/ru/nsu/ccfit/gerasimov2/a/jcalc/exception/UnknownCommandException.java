package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

public class UnknownCommandException extends CommandException {

    public UnknownCommandException(String cmdName) {
        super("Unknown command: " + cmdName);
    }
}
