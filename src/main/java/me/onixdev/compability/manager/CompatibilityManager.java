package me.onixdev.compability.manager;

import me.onixdev.compability.ICompabilityCheck;
import me.onixdev.compability.impl.LeafCompabilityWorldTicking;
import me.onixdev.compability.impl.ViaBackwardsTransactionPing;

import java.util.List;

public class CompatibilityManager {
    private boolean leafTicking;

    public void setLeafTicking(boolean leafTicking) {
        this.leafTicking = leafTicking;
    }

    public boolean isLeafTicking() {
        return leafTicking;
    }

    private List<ICompabilityCheck> checks = List.of(new LeafCompabilityWorldTicking(),new ViaBackwardsTransactionPing());
    public CompatibilityManager() {
        for (ICompabilityCheck check : checks) {
            check.check(this);
        }
    }
}
