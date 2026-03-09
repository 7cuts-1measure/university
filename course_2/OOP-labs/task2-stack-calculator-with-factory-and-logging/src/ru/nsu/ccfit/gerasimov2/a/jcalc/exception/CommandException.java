package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

public class CommandException extends CalculatorException {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

}
