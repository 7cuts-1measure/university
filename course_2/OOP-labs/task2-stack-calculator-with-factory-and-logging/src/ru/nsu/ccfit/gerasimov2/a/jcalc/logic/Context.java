package ru.nsu.ccfit.gerasimov2.a.jcalc.logic;

import java.io.PrintStream;
import java.util.Collection;
import java.util.logging.Logger;

import ru.nsu.ccfit.gerasimov2.a.jcalc.LogUtil;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.factory.Factory;

/**
 * Access to the memory and stack
 */
public class Context {
    static Logger LOGGER = LogUtil.getLogger(Context.class.getSimpleName());;

    private Memory memory;
    private Stack stack;
    public final PrintStream out;
    private boolean shouldClose = false;
    public final Factory factory;

    public Context(PrintStream out, Factory factory) {
        LOGGER.info("Initialize " + Context.class.getSimpleName());
        this.out = out;
        this.factory = factory;
        memory = new Memory();
        stack = new Stack();
    }

    public Memory getMemory() {
        return memory;
    }

    public Stack getStack() {
        return stack;
    }

    public boolean shouldClose() {
        return shouldClose;
    }

    public void setShouldClose(boolean shouldClose) {
        LOGGER.info("shouldClose was set");
        this.shouldClose = shouldClose;
    }

    public Collection<String> getCommandsClassNames() {
        return factory.getCommandClassNames();
    }
}
