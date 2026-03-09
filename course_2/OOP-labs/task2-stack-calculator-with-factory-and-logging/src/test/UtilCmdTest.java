package test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.util.CommentCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.util.ExitCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.util.HelpCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.factory.Factory;

public class UtilCmdTest extends BaseTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private Context ctx;


    @Before
    public void setUpStreams() {
        ctx = new Context(new PrintStream(outContent), new Factory());
    }
    
    @After
    public void restoreStreams() {
    }

    @Test
    public void dummyCommand() {
        Command cmd = new CommentCommand();
        cmd.execute(ctx, null);        
    }

    @Test
    public void exitCommand() {
        Command cmd = new ExitCommand();
        assertFalse(ctx.shouldClose());

        cmd.execute(ctx, null);        
        assertTrue(ctx.shouldClose());
    }

    @Test
    public void helpCommand() {
        Command cmd = new HelpCommand();
        cmd.execute(ctx, null);        
    }

    

    

}
