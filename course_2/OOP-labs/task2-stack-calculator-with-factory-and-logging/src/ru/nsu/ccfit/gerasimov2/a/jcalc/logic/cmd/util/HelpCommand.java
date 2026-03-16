package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.util;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.BaseCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;

public class HelpCommand extends BaseCommand {

    @Override
    public void execute(Context ctx, String[] args) throws CommandException {
        System.out.println("This is a simple stack calculator written in Java");
        System.out.println("You can run it in interactive mode or for interpretating a file");
        System.out.println("Command can be both in lowercase or in UPPERCASE");
        System.out.println();
        System.out.println("List of available commands:");
        System.out.println("not implemented!!");
        // try {
        //     for (String cmdClassName : ctx.getCommandsClassNames()) {
        //         String keyword = ctx.factory.getKeywordForClassName(cmdClassName);
        //         Command cmd = (Command) Class.forName(cmdClassName).getDeclaredConstructor().newInstance();
        //         String description = cmd.getDescription();
        //         System.out.printf("\t%s\t\t    %s\n\n", keyword, description);
        //     }
        // } catch (ReflectiveOperationException e) {
        //     throw new IllegalStateException("Factory has a class but cannot create it: " + e.getLocalizedMessage());
        // }
        
    }
    @Override
    public String getDescription() {
        return "Print help message";
    }

}
