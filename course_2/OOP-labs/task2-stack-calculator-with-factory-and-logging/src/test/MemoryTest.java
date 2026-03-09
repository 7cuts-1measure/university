package test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.InvalidArgumentException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.StackUnderflowException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.VarException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Stack;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.stack.PushCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.memory.DefineCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.stack.PopCommand;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.stack.PrintCommand;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MemoryTest extends BaseTest {

    private void testVar(String name, Double value) {
    
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {}
        }       
        ));

        Command cmd = new DefineCommand();
        String[] args = {name, value.toString()};
        cmd.execute(ctx, args);

        assertEquals(ctx.getMemory().getVar(name), value, 0.001);
    
    }


    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private Context ctx;


    @Before
    public void setUpStreams() {
        ctx = new Context(new PrintStream(outContent), null);
    }

    @After
    public void restoreStreams() {
    }


    @Test
    public void basicTests() {
        testVar("abacaba", 10.345);
        testVar("name", 0.000123);
    }

    @Test 
    public void nameBeginsWithDigits() {
        assertThrows(VarException.class, 
            () -> { testVar("123name", 1.25 );} 
        );
    }

    @Test 
    public void define() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {}
        }       
        ));

        Command cmd = new DefineCommand();
        String[] args = {"varName", "12.5"};
        cmd.execute(ctx, args);

        Double actual = ctx.getMemory().getVar("varName");
        assertEquals(12.5, actual, 0.001f);
    }


    @Test 
    public void storeAndLoadByCommandPop() {
        final String varName = "varkdsakdl";
        final String varValue = "12.5";

        Command cmdDefine = new DefineCommand();
        Command cmdPop = new PopCommand();
        Command cmdPush = new PushCommand();

        String[] argsDefine = {varName, varValue};
        String[] argsPush = {varName};
        String[] argsPop = {};

        cmdDefine.execute(ctx, argsDefine);
        cmdPush.execute(ctx, argsPush);


        System.setOut(new PrintStream(outContent));
        cmdPop.execute(ctx, argsPop);
        
        assertEquals(varValue + "\n", outContent.toString());
    }

    @Test 
    public void useNonExitsVarName() {
        ctx.getMemory().defineVar("varName", 0);
        assertThrows(VarException.class, () -> {
            ctx.getMemory().getVar("another name");
        });
    }

    @Test
    public void defineEmptyNameVar() {
        assertThrows(VarException.class, () -> {
            ctx.getMemory().defineVar("", 0);
        });

    }

    @Test
    public void stackUnderflow() {
        Command cmd = new PopCommand();
        assertThrows(StackUnderflowException.class, () -> {
                cmd.execute(ctx, new String[]{});
            }
        );
    }

    @Test
    public void stackPushPop() {
        Stack stack = ctx.getStack();
        double expected = 12.5;
        stack.push(expected);
        double actual = stack.pop();

        assertEquals(expected, actual, 0.001);
    }

     @Test 
    public void storeAndPrint() {
        final String varName = "varkdsakdl";
        final String varValue = "12.5";

        Command cmdDefine = new DefineCommand();
        Command cmdPrint = new PrintCommand();
        Command cmdPush = new PushCommand();

        String[] argsDefine = {varName, varValue};
        String[] argsPush = {varName};
        String[] argsPrint = {};

        cmdDefine.execute(ctx, argsDefine);
        cmdPush.execute(ctx, argsPush);


        System.setOut(new PrintStream(outContent));
        cmdPrint.execute(ctx, argsPrint);
        
        assertEquals(varValue + "\n", outContent.toString());
    }



    @Test
    public void WrongArguments() {
        Command cmd = new PushCommand();
        assertThrows(InvalidArgumentException.class, () -> {
            cmd.execute(ctx, new String[]{"1st arg", "2nd arg", "3rd arg"});
        });
    }

    @Test 
    public void PrintWhenStackIsEmpty() {
        Command cmdPrint = new PrintCommand();
        String[] argsPrint = {};

        System.setOut(new PrintStream(outContent));
        cmdPrint.execute(ctx, argsPrint);
        
        assertEquals("\n", outContent.toString());
    }


}
