package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.InvalidArgumentException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;

public abstract class BaseCommand implements Command {



    /**
     * @param args arguments for command
     * @param number number of arguments
     * @throws InvalidArgumentException if arguments are incorrect: args.length !+ number
     */
    public static void validateArgs(String[] args, Integer number) throws InvalidArgumentException {
        if (args.length != number) {
            throw new InvalidArgumentException("Expected " + number + " arguments (get: " + args.length + ")");
        }
    }

    /**
     *  If token is a number - just parsing it.
     *  If token is a variable name - try to find it in memory
     * @param ctx
     * @param token variable or double literal
     * @return value of token
     */
    public Double parseToken(Context ctx, String token) {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            return ctx.getMemory().getVar(token);
        }
    }
}
