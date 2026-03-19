package me.onixdev.compability.manager

import me.onixdev.compability.ICompabilityCheck
import me.onixdev.compability.impl.LeafCompabilityWorldTicking
import me.onixdev.compability.impl.PlugmanCompability
import me.onixdev.compability.impl.ViaBackwardsTransactionPing

class CompatibilityManager {
    var isLeafTicking: Boolean = false
    var isHasPlugman: Boolean = false

    private val checks: MutableList<ICompabilityCheck> =
        mutableListOf(LeafCompabilityWorldTicking(), ViaBackwardsTransactionPing(), PlugmanCompability())

    init {
        for (check in checks) {
            check.check(this)
        }
    }
}