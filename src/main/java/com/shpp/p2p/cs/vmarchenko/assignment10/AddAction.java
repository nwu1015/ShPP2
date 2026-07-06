package com.shpp.p2p.cs.vmarchenko.assignment10;

public class AddAction implements IAction {
    @Override
    public double execute(double... args) {
        return args[0] + args[1];
    }
}
