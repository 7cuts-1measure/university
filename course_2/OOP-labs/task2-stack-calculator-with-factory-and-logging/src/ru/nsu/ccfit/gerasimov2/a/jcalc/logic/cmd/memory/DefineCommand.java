package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.memory;

import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.BaseCommand;

public class DefineCommand extends BaseCommand {

    @Override
    public void execute(Context ctx, String[] args) {
        validateArgs(args, 2); // DEFINE a 4
        String varName = args[0];
        double value = parseToken(ctx, args[1]);
        ctx.getMemory().defineVar(varName, value);
    }

    @Override
    public String getDescription() {
        return "Define a variable. 1st operand is a variable name, 2nd - its value";
    }

}
