package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.arithmetic;

import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.BaseCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Stack;


public class MultCommand extends BaseCommand {

    @Override
    public void execute(Context ctx, String[] args) throws CommandException {
        validateArgs(args, 0);
        Stack stack = ctx.getStack();

        double second = stack.pop();
        double first = stack.pop();

        stack.push(first * second);
    }

    @Override
    public String getDescription() {
        return "Multiplication";
    }

}
