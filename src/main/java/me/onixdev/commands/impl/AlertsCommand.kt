package me.onixdev.commands.impl

import me.onixdev.OnixAnticheat
import me.onixdev.commands.api.OnixCommandBase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class AlertsCommand : OnixCommandBase("alerts") {
    override fun getDescription(): String {
        return ""
    }

    override fun getMinArgs(): Int {
        return 0
    }

    override fun getMaxArgs(): Int {
        return 0
    }

    override fun noConsole(): Boolean {
        return true
    }

    override fun getPermission(): String {
        return "onix.alerts"
    }

    override fun onCommand(sender: CommandSender, args: Array<String>): Boolean {
        val p = sender as Player
        val user = OnixAnticheat.INSTANCE.playerDatamanager[p.uniqueId]
        user?.alertManager?.toggleAlerts()
        return false
    }

    override fun onTabComplete(sender: CommandSender, args: Array<String>): List<String> {
        return listOf()
    }
}
