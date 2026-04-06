package me.onixdev.commands.api

import com.google.common.collect.ImmutableList
import dev.onixac.api.command.OnixCommandBase
import dev.onixac.api.manager.ICommandManager
import me.onixdev.commands.impl.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*


class CommandManager : TabExecutor, ICommandManager {
    private val commands: MutableList<OnixCommandBase> = ArrayList<OnixCommandBase>()

    init {
        registerCommmand(VerboseCommands())
        registerCommmand(ReloadCommand())
        registerCommmand(MitigateCommand())
        registerCommmand(AlertsCommand())
        registerCommmand(DebugCommand())
        registerCommmand(ProfileCommand())
        registerCommmand(AutoSetupCommand())
        registerCommmand(AnimationCommand())
        registerCommmand(ChecksCommand())
        registerCommmand(KickCommand())
    }

    override fun onCommand(
        commandSender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (!label.equals("onix", ignoreCase = true)) {
            commandSender.sendMessage("Usage: /" + "onix")
            return true
        }

        if (args.isEmpty()) {
            return true
        }
        val arg = args[0]
        for (subCommand in commands) {
            if (!subCommand.getName().equals(arg, ignoreCase = true)) {
                continue
            }
            if (subCommand.noConsole() && commandSender !is Player) {
                commandSender.sendMessage("Only Player")
                return true
            }
            if (!subCommand.hasPermission(commandSender)) {
                commandSender.sendMessage("You don't have permission!")
                return true
            }
            val processedArgs = this.processArgs(args)
            if (processedArgs.size > subCommand.getMaxArgs() || processedArgs.size < subCommand.getMinArgs()) {
                commandSender.sendMessage("Usage: " + subCommand.usage)
                return true
            }
            return subCommand.onCommand(commandSender, processedArgs)
        }
        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): MutableList<String> {
        if (!label.equals("onix", ignoreCase = true)) {
            return ImmutableList.of<String>()
        }
        if (args.size == 1) {
            val possibleCommands: MutableList<String> = ArrayList<String>()
            val arg = args[0]

            for (Command in commands) {
                if (Command.noConsole() && commandSender !is Player) {
                    continue
                }
                if (!Command.hasPermission(commandSender)) {
                    continue
                }
                if (arg.isEmpty()) {
                    possibleCommands.add(command.name)
                } else if (Command.getName().startsWith(arg.lowercase())) {
                    possibleCommands.add(Command.getName())
                }
            }
            return possibleCommands
        }

        for (Command in commands) {
            if (!Command.getName().equals(args[0], ignoreCase = true)) {
                continue
            }
            if (Command.noConsole() && commandSender !is Player) {
                return ImmutableList.of<String>()
            }
            if (!Command.hasPermission(commandSender)) {
                return ImmutableList.of<String>()
            }
            val processedArgs = this.processArgs(args)
            return Command.onTabComplete(commandSender, processedArgs)
        }
        return ImmutableList.of<String>()
    }

    private fun processArgs(args: Array<String>): Array<String?> {
        val tempArgs = arrayOfNulls<String>(args.size - 1)
        System.arraycopy(args, 1, tempArgs, 0, args.size - 1)
        return tempArgs
    }

    /**
     * @param name
     * @return Возвращает комманду если не найдет
     * @since 1.0
     */
    override fun getCommand(name: String?): Optional<OnixCommandBase> {
        for (command in commands) {
            if (command.getName() == name) return Optional.of<OnixCommandBase>(command)
        }
        return Optional.empty<OnixCommandBase>()
    }

    /**
     * @param command
     * @since 1.0
     */
    override fun registerCommmand(command: OnixCommandBase?) {
        if (command == null || command.getName().isBlank()) return
        commands.add(command)
    }

    /**
     * @param name@since 1.0
     */
    override fun unregisterCommmand(name: String?) {
        try {
            var index = 0
            for (command in commands) {
                if (command.getName() == name) {
                    //commands.remove(command);
                    break
                }
                ++index
            }
            commands.removeAt(index)
        } catch (ignored: ConcurrentModificationException) {
        }
    }
}
