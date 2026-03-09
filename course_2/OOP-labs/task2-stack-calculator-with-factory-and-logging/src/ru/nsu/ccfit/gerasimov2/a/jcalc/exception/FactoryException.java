package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

public class FactoryException extends CalculatorException {

    public FactoryException(String message) {
        super(message);
    }

    public FactoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
