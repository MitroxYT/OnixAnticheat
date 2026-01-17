package me.onixdev.commands.impl

import dev.onixac.api.command.OnixCommandBase
import me.onixdev.autosetup.util.SetupStage
import me.onixdev.util.color.MessageUtil
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender

class AutoSetupCommand() : OnixCommandBase("setup") {
    private var setupStage: SetupStage = SetupStage.NOSETUP
    override fun getDescription(): String? {
        return "Auto Setup Command"
    }

    override fun getMinArgs(): Int {
        return 1
    }

    override fun getMaxArgs(): Int {
        return 99
    }

    override fun noConsole(): Boolean {
        return true
    }

    override fun onCommand(
        sender: CommandSender,
        args: Array<out String?>?
    ): Boolean {
        if (!args.isNullOrEmpty()) {
            if (args[0].equals("start")) {
                setupStage = SetupStage.THEMA
                sender.spigot().sendMessage(buildClickableMessage("/onix setup theme box","<aqua>Нажмите чтобы выбрать тематику сервера бокс пвп"))
                sender.spigot().sendMessage(buildClickableMessage("/onix setup theme anka","<aqua>Нажмите чтобы выбрать тематику сервера Анархия"))
            } else if (args[0].equals("stop")) {

            }
            else if (args[0]!!.startsWith("theme")) {
                val thema = args[1]
                if (thema != null) {
                    if (thema == "box") {
                        MessageUtil.sendMessage(sender, MessageUtil.miniMessage("<aqua>Вы выбрали тему сервера бокс пвп ждите античит настраивает оптимальный конфиг"))
                    }
                }
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender?,
        args: Array<out String?>?
    ): List<String?>? {
        return mutableListOf<String?>("")
    }
    fun buildClickableMessage(command: String,message:String) : TextComponent{
        val container = TextComponent(MessageUtil.translate(message))
        container.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
        return container
    }
}