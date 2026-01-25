package me.onixdev.check.impl.combat.heuristics;

public class AimHeuristicAS {
    private boolean obsEnt;
    static boolean isObserving(AimHeuristicAS storage) {
        return storage.obsEnt;
    }

    static boolean setObserving(AimHeuristicAS storage, boolean value) {
        return storage.obsEnt = value;
    }
}
