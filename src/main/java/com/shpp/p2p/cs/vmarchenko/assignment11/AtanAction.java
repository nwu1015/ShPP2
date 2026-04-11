package com.shpp.p2p.cs.vmarchenko.assignment11;

public class AtanAction implements IAction {
    @Override
    public double execute(double... args) {
        return Math.atan(args[0]);
    }
}
