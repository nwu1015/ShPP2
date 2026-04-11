package com.shpp.p2p.cs.vmarchenko.assignment11;

public class SinAction implements IAction {
    @Override
    public double execute(double... args) {
        return Math.sin(args[0]);
    }
}

