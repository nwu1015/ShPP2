package com.shpp.p2p.cs.vmarchenko.assignment11;

public class DivideAction implements IAction {
    @Override
    public double execute(double... args) {
        if (args[1] == 0) {
            throw new ArithmeticException("Divide by zero");
        }

        return args[0] / args[1];
    }
}
