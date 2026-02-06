package me.onixdev.compability.impl;

import me.onixdev.OnixAnticheat;
import me.onixdev.compability.ICompabilityCheck;
import me.onixdev.compability.manager.CompatibilityManager;

public class ViaBackwardsTransactionPing implements ICompabilityCheck {
    @Override
    public void check(CompatibilityManager manager) {
        try {
            Class.forName("com.viaversion.viabackwards.ViaBackwards");
            OnixAnticheat.INSTANCE.printCool("&bОбнаружен Via Backwards Включаю поддержку");
            System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");
        } catch (ClassNotFoundException ignored) {}
    }
}
