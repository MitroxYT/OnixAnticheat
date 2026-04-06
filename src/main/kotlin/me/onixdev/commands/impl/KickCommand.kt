package me.onixdev.commands.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect
import dev.onixac.api.command.OnixCommandBase
import me.onixdev.util.color.MessageUtil
import me.onixdev.util.extend.KotlinExtends.getData
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class KickCommand : OnixCommandBase("kick") {
    override fun getDescription(): String {
       return ""
    }

    override fun getUsage(): String {
        return "/onix kick name <red> kick message"
    }

    override fun getMinArgs(): Int {
       return 2
    }

    override fun getMaxArgs(): Int {
        return 184848
    }

    override fun noConsole(): Boolean {
        return false
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String?>?
    ): Boolean {
        if (args?.size!! > 1) {
            val name = args[0]
            val player = Bukkit.getServer().getPlayer(name.toString())?.getData() ?: return false
            val command = getCommand(args)
            if (command.lowercase().contains("§")) {
                MessageUtil.sendMessage(sender, MessageUtil.miniMessage("<red> использование § недопустимо"))
                return false
            }
            player.disconnect(command)
        }
        return true
    }
    private fun getCommand(args: Array<out String?>): String {
        return args.copyOfRange(1, args.size)
            .filterNotNull()
            .joinToString(" ")
    }
    override fun onTabComplete(
        sender: CommandSender?,
        args: Array<out String?>?
    ): List<String?>? {
        if (args!!.size == 1) {
            return getPlayersFirst(args[0])
        }
        return emptyList()
    }
}