package com.shpp.p2p.cs.vmarchenko.assignment11;

public class Log2Action implements IAction {
    @Override
    public double execute(double... args) {
        return Math.log(args[0]) / Math.log(2);
    }
}
