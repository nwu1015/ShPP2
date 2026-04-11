package com.shpp.p2p.cs.vmarchenko.assignment11;

public class Log10Action implements IAction {
    @Override
    public double execute(double... args) {
        return Math.log10(args[0]);
    }
}
