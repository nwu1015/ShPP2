package com.shpp.p2p.cs.vmarchenko.assignment11;

public class TanAction implements IAction {
    @Override
    public double execute(double... args) {
        return Math.tan(args[0]);
    }
}
