package me.onixdev.commands.impl

import me.onixdev.OnixAnticheat
import me.onixdev.commands.api.OnixCommandBase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugCommand : OnixCommandBase("debug") {
    override fun getDescription(): String {
        return "toggle debug"
    }

    override fun getMinArgs(): Int {
        return 0
    }

    override fun getPermission(): String {
        return "onix.debug"
    }

    override fun getMaxArgs(): Int {
        return 99
    }

    override fun noConsole(): Boolean {
        return true
    }

    override fun onCommand(sender: CommandSender, args: Array<out String>?): Boolean {
        val p = sender as Player
        val user = OnixAnticheat.INSTANCE.playerDatamanager[p.uniqueId]
        user?.toggleDebug();
        return true
    }

    override fun onTabComplete(sender: CommandSender?, args: Array<out String>?): MutableList<String> {
        TODO("Not yet implemented")
    }

}