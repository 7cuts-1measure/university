package ru.nsu.ccfit.gerasimov2.a.jcalc.logic;

import java.util.EmptyStackException;

import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.StackUnderflowException;

public class Stack {
    java.util.Stack<Double> stack = new java.util.Stack<>();
    
    public Double pop() throws StackUnderflowException{
        try {
            return stack.pop();
        } catch (EmptyStackException e) {
            throw new StackUnderflowException("Stack is empty");
        }
    }

    public void push(double value) {
        stack.push(value);
    }

    public Double peek() throws StackUnderflowException {
        try {
            return stack.pop();
        } catch (EmptyStackException e) {
            throw new StackUnderflowException("Stack is empty");
        }
    }
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}  
