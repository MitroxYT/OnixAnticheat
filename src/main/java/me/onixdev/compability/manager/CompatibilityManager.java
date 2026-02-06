package me.onixdev.compability.manager;

import me.onixdev.compability.ICompabilityCheck;
import me.onixdev.compability.impl.LeafCompabilityWorldTicking;
import me.onixdev.compability.impl.PlugmanCompability;
import me.onixdev.compability.impl.ViaBackwardsTransactionPing;

import java.util.List;

public class CompatibilityManager {
    private boolean leafTicking;
    private boolean hasPlugman;

    public void setLeafTicking(boolean leafTicking) {
        this.leafTicking = leafTicking;
    }

    public boolean isLeafTicking() {
        return leafTicking;
    }

    public boolean isHasPlugman() {
        return hasPlugman;
    }

    public void setHasPlugman(boolean hasPlugman) {
        this.hasPlugman = hasPlugman;
    }

    private List<ICompabilityCheck> checks = List.of(new LeafCompabilityWorldTicking(),new ViaBackwardsTransactionPing(),new PlugmanCompability());
    public CompatibilityManager() {
        for (ICompabilityCheck check : checks) {
            check.check(this);
        }
    }
}
