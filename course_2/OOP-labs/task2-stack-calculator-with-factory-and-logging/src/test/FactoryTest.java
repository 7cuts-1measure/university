package test;

import static org.junit.Assert.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.FactoryException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.UnknownCommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.factory.Factory;

public class FactoryTest extends BaseTest {
    
    Factory factory;
    Context ctx;
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();

    @Before
    public void setUp() throws FactoryException {
        this.factory = new Factory();
        this.ctx = new Context(new PrintStream(stdout), factory);
    }

    @Test
    public void createCommand() throws CommandException{
        Command cmd = factory.newCommand("help");
        cmd.execute(ctx, new String[]{});
    }

    @Test
    public void wrongCommand() {
        assertThrows(UnknownCommandException.class, () -> {
            factory.newCommand("ADSLDSKDWRK)20o13-21390kodsa");
        });
    }

    @Test
    public void emptyCommand() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.newCommand("");
        });
    }
}
