package com.shpp.p2p.cs.vmarchenko.assignment11;

public class PowAction implements IAction{
    @Override
    public double execute(double... args) {
        return Math.pow(args[0], args[1]);
    }
}
