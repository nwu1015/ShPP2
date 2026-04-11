package com.shpp.p2p.cs.vmarchenko.assignment11;

public class CosAction implements IAction{
    @Override
    public double execute(double... args) {
        return Math.cos(args[0]);
    }
}
