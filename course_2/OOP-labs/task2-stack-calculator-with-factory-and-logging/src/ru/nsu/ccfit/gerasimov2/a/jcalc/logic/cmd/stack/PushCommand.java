package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.stack;

import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.BaseCommand;

public class PushCommand extends BaseCommand {

    @Override
    public void execute(Context ctx, String[] args) {
        validateArgs(args, 1);
        double x = parseToken(ctx, args[0]);
        ctx.getStack().push(x);
    }

    @Override
    public String getDescription() {
        return "Push 1st operand (var or number) on a stack";
    }

}
