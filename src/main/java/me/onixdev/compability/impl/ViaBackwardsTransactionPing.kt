package me.onixdev.compability.impl

import me.onixdev.OnixAnticheat
import me.onixdev.compability.ICompabilityCheck
import me.onixdev.compability.manager.CompatibilityManager

class ViaBackwardsTransactionPing : ICompabilityCheck {
    override fun check(manager: CompatibilityManager?) {
        try {
            Class.forName("com.viaversion.viabackwards.ViaBackwards")
            OnixAnticheat.INSTANCE.printCool("&bОбнаружен Via Backwards Включаю поддержку")
            System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true")
        } catch (_: ClassNotFoundException) {
        }
    }
}