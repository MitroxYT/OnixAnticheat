package me.onixdev.commands.impl

import dev.onixac.api.command.OnixCommandBase
import me.onixdev.OnixAnticheat
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class AnimationCommand : OnixCommandBase("punish") {
    override fun getDescription(): String? {
        return "execute command and punish player"
    }

    override fun getMinArgs(): Int {
      return 2
    }

    override fun getMaxArgs(): Int {
        return 999999
    }

    override fun noConsole(): Boolean {
        return false
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String?>?
    ): Boolean {
        if (args != null) {
            val name= args[0]
            val player = args[1]
            val bplayer = Bukkit.getServer().getPlayer(player.toString())
            val command = getCommand(args)
            if (command.isNotBlank()) {
                OnixAnticheat.INSTANCE.animationManager.startPunishment(name,bplayer,command)
            }
            sender.sendMessage("Анимация запущена")
        }
        return false
    }

    private fun getCommand(args: Array<out String?>): String {
        if (args.size <= 2) {
            return ""
        }
        return args.copyOfRange(2, args.size)
            .filterNotNull()
            .joinToString(" ")
    }

    override fun onTabComplete(
        sender: CommandSender?,
        args: Array<out String?>?
    ): List<String?>? {
        if (args != null) {
            if (args.size == 1) {
                return listOf("Thunder")
            }
            if (args.size == 2) {
                return Bukkit.getOnlinePlayers().map { it.name }
            }
        }
        return emptyList()
    }
}