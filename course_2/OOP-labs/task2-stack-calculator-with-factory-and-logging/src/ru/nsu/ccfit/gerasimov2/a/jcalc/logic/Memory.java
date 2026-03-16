package ru.nsu.ccfit.gerasimov2.a.jcalc.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ru.nsu.ccfit.gerasimov2.a.jcalc.LogUtil;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.VarException;

public class Memory {
    private Map<String, Double> varTable;
    static Logger LOGGER = LogUtil.getLogger(Memory.class.getSimpleName());;

    /**
     * Stores the variable to the memory
     * 
     * @param name
     * @param value
     */
    public void defineVar(String name, double value) throws VarException {
        LOGGER.fine("Defining " + name + " = " + String.valueOf(value) );
        checkVarName(name);
        varTable.put(name, value);
    }

    private void checkVarName(String name) throws VarException {
        if (name.isEmpty()) {
            LOGGER.fine("Var name is empty");
            throw new VarException("Cannot create var with empty name"); // TODO: make a WrongVariableNameException
        }
        char first = name.charAt(0);
        if (Character.isDigit(first)) {
            LOGGER.fine("Var name starts with digit. Ignoring");
            throw new VarException("The name of a var shouldn't starts with digit");
        }

    }

    public Memory() {
        LOGGER.info("Initialize " + Memory.class.getSimpleName());
        varTable = new HashMap<String, Double>();
    }

    /**
     * Trying to find a value in the memory
     * 
     * @param name
     * @return Value of the var with this name
     * @throws VarException if no var is defined with this name
     */
    public double getVar(String name) throws VarException {
        LOGGER.fine("Finding var " + name);
        if (!varTable.containsKey(name)) {
            LOGGER.fine("Cannot find " + name);
            throw new VarException("Var " + name + " is not defined.");
        }
        LOGGER.fine("Var" + name + "is founded");
        double x = varTable.get(name);
        return x;
    }
}
