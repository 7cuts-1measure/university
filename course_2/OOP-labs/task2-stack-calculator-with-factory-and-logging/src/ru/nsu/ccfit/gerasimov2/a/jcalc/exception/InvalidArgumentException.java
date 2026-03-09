package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

public class InvalidArgumentException extends CommandException {

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

}
