package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd;


import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.InvalidArgumentException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;

public interface Command{
    /**
     * Execute the command. Every command such as "+", "-", "DEFINE" should implement iterface Command;
     * Onlny if class implements this interface, calculator can call it and use
     * @param ctx  Access to the memory and stack
     * @param args Array of arguments in C style: args[0] is the name of command,
     *             args[1] is the first agrument
     */
    void execute(Context ctx, String[] args) throws CommandException;

    String getDescription();
}