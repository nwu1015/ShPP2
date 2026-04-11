package com.shpp.p2p.cs.vmarchenko.assignment11;

public class SqrtAction implements IAction {
    @Override
    public double execute(double... args) {
        return Math.sqrt(args[0]);
    }
}
