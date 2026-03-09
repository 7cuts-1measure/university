package ru.nsu.ccfit.gerasimov2.a.jcalc.exception;

public class StackUnderflowException extends CalculatorException {

    public StackUnderflowException(String message, Throwable cause) {
        super(message, cause);
    }

    public StackUnderflowException(String message) {
        super(message);
    }

}
