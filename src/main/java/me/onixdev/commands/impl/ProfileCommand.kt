package me.onixdev.commands.impl

import me.onixdev.OnixAnticheat
import me.onixdev.commands.api.OnixCommandBase
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class ProfileCommand : OnixCommandBase("profile") {
    override fun getDescription(): String {
      return  "profile"
    }

    override fun getMinArgs(): Int {
    return    1
    }

    override fun getMaxArgs(): Int {
      return  2
    }

    override fun noConsole(): Boolean {
      return  false
    }

    override fun onCommand(sender: CommandSender, args: Array<out String>?): Boolean {
        System.out.println("a: " + args!![0])
        val playerName = args!![0]
        val bplayer = Bukkit.getPlayer(playerName)
        if (bplayer != null) {
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(bplayer.uniqueId)
            if (user != null) {
                val msg = OnixAnticheat.INSTANCE.configManager.proFileMsg.replace("%player%",user.name).replace("%version%",user.user.clientVersion.releaseName)
                sender.sendMessage(msg)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender?, args: Array<out String>?): MutableList<String> {
     //   TODO("Not yet implemented")
        return mutableListOf()
    }
}