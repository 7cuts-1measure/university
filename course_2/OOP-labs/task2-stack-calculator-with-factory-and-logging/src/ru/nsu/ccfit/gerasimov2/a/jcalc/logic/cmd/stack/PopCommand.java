package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.stack;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.StackUnderflowException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.BaseCommand;

public class PopCommand extends BaseCommand {

    @Override
    public void execute(Context ctx, String[] args) {
        validateArgs(args, 0);
        if (ctx.getStack().isEmpty()) {
            throw new StackUnderflowException("Stack underflow");
        }
        double x = ctx.getStack().pop();
        System.out.println(x);
    }

    @Override
    public String getDescription() {
        return "Pop element from the top of stack";
    }

}
