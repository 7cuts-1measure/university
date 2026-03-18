package ru.nsu.ccfit.gerasimov2.a.factory;

/** Any error that happens in the factory */
public class FactoryException extends Exception {
    public FactoryException(String msg) {
        super(msg);
    }
    public FactoryException() {
        super();
    }
}
