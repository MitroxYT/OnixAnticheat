package me.onixdev.compability.impl

import me.onixdev.compability.ICompabilityCheck
import me.onixdev.compability.manager.CompatibilityManager
import org.bukkit.Bukkit

class PlugmanCompability : ICompabilityCheck {
    override fun check(manager: CompatibilityManager?) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlugmanX")) manager?.isHasPlugman = true
    }
}