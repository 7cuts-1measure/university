package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.StackUnderflowException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.Context;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.arithmetic.*;

public class ArtithmeticCmdTest extends BaseTest {

    private void test2args(Command cmd, double a, double b, double expected) throws CommandException {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {}
        }       
        ));
        Context ctx = new Context(System.out, null);
        var stack = ctx.getStack();
        stack.push(a);
        stack.push(b);
        cmd.execute(ctx, new String[] {});
        assertEquals(expected, ctx.getStack().pop(), 0.001f);
    }
    private void test1arg(Command cmd, double x, double expected) throws CommandException {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int arg0) throws IOException {}
        }       
        ));
        
        Context ctx = new Context(System.out, null);
        var stack = ctx.getStack();
        stack.push(x);
        cmd.execute(ctx, new String[] {});
        assertEquals(expected, ctx.getStack().pop(), 0.001f);
    }

    private void testEmptyStack(Command cmd) {
        Context ctx = new Context(System.out, null);
        assertThrows(StackUnderflowException.class, () -> { cmd.execute(ctx, new String[] {}); });
        assertThrows(StackUnderflowException.class, () -> { ctx.getStack().peek(); });
    }

    @Test
    public void emptyStack() {
        testEmptyStack(new PlusCommand());
        testEmptyStack(new MinusCommand());
        testEmptyStack(new MultCommand());
        testEmptyStack(new DivideCommand());
        testEmptyStack(new SqrtCommand());
    }

    @Test
    public void plus() throws CommandException {
        Command cmd = new PlusCommand();
        int a = 2;
        int b = 3;
        test2args(cmd, a, b, a + b);
    }

    @Test
    public void plusZero() throws CommandException {
        Command cmd = new PlusCommand();
        int a = 2;
        int b = 0;
        test2args(cmd, a, b, a);
    }

    @Test
    public void plusZeroZero() throws CommandException {
        Command cmd = new PlusCommand();
        int a = 0;
        int b = 0;
        test2args(cmd, a, b, 0);
    }

    @Test
    public void minus() throws CommandException {
        Command cmd = new MinusCommand();
        double a = 2;
        double b = 3;
        test2args(cmd, a, b, a - b);
    }

    @Test
    public void minusZero() throws CommandException{
        Command cmd = new MinusCommand();
        int a = 2;
        int b = 0;
        test2args(cmd, a, b, a);
    }

    @Test
    public void minusZeroZero() throws CommandException {
        Command cmd = new MinusCommand();
        int a = 0;
        int b = 0;
        test2args(cmd, a, b, 0);
    }

    @Test
    public void divide() throws CommandException {
        Command cmd = new DivideCommand();
        double a = 2;
        double b = 3;
        test2args(cmd, a, b, b / a);
    }

    @Test
    public void dividePositiveByZero() throws CommandException {
        Command cmd = new DivideCommand();
        double a = 0;
        double b = 2;
        test2args(cmd, a, b, Double.POSITIVE_INFINITY);
    }

    @Test
    public void divideNegativeByZero() throws CommandException {
        Command cmd = new DivideCommand();
        double a = 0;
        double b = -2;
        test2args(cmd, a, b, Double.NEGATIVE_INFINITY);
    }

    @Test
    public void divideZeroByZero() throws CommandException {
        Command cmd = new DivideCommand();
        Context ctx = new Context(System.out, null);
        var stack = ctx.getStack();

        double a = 0;
        double b = 0;

        stack.push(a);
        stack.push(b);
        
        cmd.execute(ctx, new String[] {});
        Double actual = ctx.getStack().pop();
        assertTrue(actual.isNaN());

        test2args(cmd, a, b, Double.NaN);
    }

    @Test
    public void mult() throws CommandException {
        Command cmd = new MultCommand();
        double a = 2;
        double b = 3;
        test2args(cmd, a, b, a * b);
    }

    @Test
    public void multZero() throws CommandException {
        Command cmd = new MultCommand();
        double a = 2;
        double b = 0;
        test2args(cmd, a, b, a * b);
    }

    @Test 
    public void sqrt() throws CommandException {
        Command cmd = new SqrtCommand();
        double x = 2;
        test1arg(cmd, x, Math.sqrt(x));
    }

    @Test 
    public void sqrtZero() throws CommandException {
        test1arg(new SqrtCommand(), 0, 0); 
    }

    @Test
    public void plusManyValues()throws CommandException {
        Command cmd = new PlusCommand();
        for (double a = -100; a < 100; a++) {
            for (double b = -100;  b < 100; b++) {
                test2args(cmd, a, b, a + b);        
            }
        }
    }

    @Test
    public void minusManyValues() throws CommandException{
        Command cmd = new MinusCommand();
        for (double a = -100; a < 100; a++) {
            for (double b = -100;  b < 100; b++) {
                test2args(cmd, a, b, a - b);        
            }
        }
    }

    @Test
    public void multManyValues() throws CommandException{
        Command cmd = new MultCommand();
        for (double a = -100; a < 100; a++) {
            for (double b = -100;  b < 100; b++) {
                test2args(cmd, a, b, a * b);        
            }
        }
    }

    @Test
    public void divideManyValues() throws CommandException {
        Command cmd = new DivideCommand();
        for (double a = -100; a < 100; a++) {
            for (double b = -100;  b < 100; b++) {
                if (a == 0 || b == 0) {continue;}
                test2args(cmd, a, b, b / a);        
            }
        }
    }


}
