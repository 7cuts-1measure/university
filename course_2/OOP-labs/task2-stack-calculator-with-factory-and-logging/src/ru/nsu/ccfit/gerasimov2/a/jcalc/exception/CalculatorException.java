package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

/**
 * All exceptions that trows in jcalc have this type
 */
public class CalculatorException extends Exception {
    public CalculatorException(String message) {
        super(message);
    }
}