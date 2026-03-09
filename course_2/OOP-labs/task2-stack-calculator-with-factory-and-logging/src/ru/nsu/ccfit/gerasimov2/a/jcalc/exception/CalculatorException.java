package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

/**
 * All exceptions that trows in jcalc have this type
 */
public class CalculatorException extends RuntimeException {
    public CalculatorException(String message) {
        super(message);
    }

    public CalculatorException(String message, Throwable cause) {
        super(message, cause);
    }
}