package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.stack;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.BaseCommand;

public class PrintCommand extends BaseCommand {

    @Override
    public void execute(Context ctx, String[] args) throws CommandException {
        validateArgs(args, 0);
        var stack = ctx.getStack();
        ctx.out.println(stack.isEmpty() ? "" : stack.peek());
    }

    @Override
    public String getDescription() {
        return "Print value on top of stack";
    }

}
