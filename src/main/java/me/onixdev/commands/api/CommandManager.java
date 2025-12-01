package me.onixdev.commands.api;

import com.google.common.collect.ImmutableList;
import me.onixdev.commands.impl.MitigateCommand;
import me.onixdev.commands.impl.ReloadCommand;
import me.onixdev.commands.impl.VerboseCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandManager implements TabExecutor {
    private final List<OnixCommandBase> commands = List.of(new VerboseCommands(),new ReloadCommand(),new MitigateCommand());
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!commandSender.hasPermission("onix.usecommand")) {
            commandSender.sendMessage("You don't have permission!");
            return true;
        }
        if (!label.equalsIgnoreCase("onix")) {
            commandSender.sendMessage("Usage: /" + "onix");
            return true;
        }

        if (args.length == 0) {
            return true;
        }
        String arg = args[0];
        for (OnixCommandBase subCommand : commands) {
            if (!subCommand.getName().equalsIgnoreCase(arg)) {
                continue;
            }
            if (subCommand.noConsole() && !(commandSender instanceof Player)) {
                commandSender.sendMessage("Only Player");
                return true;
            }
            if (!subCommand.hasPermission(commandSender)) {
                commandSender.sendMessage("You don't have permission!");
                return true;
            }
            String[] processedArgs = this.processArgs(args);
            if (processedArgs.length > subCommand.getMaxArgs() || processedArgs.length < subCommand.getMinArgs()) {
                commandSender.sendMessage("Usage: " + subCommand.getUsage());
                return true;
            }
            return subCommand.onCommand(commandSender, processedArgs);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!commandSender.hasPermission("onix.usecommand")) {
            return ImmutableList.of();
        }
        if (!label.equalsIgnoreCase("onix")) {
            return ImmutableList.of();
        }
        if (args.length == 1) {
            List<String> possibleCommands = new ArrayList<>();
            String arg = args[0];

            for (OnixCommandBase Command : commands) {
                if (Command.noConsole() && !(commandSender instanceof Player)) {
                    continue;
                }
                if (!Command.hasPermission(commandSender)) {
                    continue;
                }
                if (arg.isEmpty()) {
                    possibleCommands.add(command.getName());
                } else if (Command.getName().startsWith(arg.toLowerCase(Locale.ROOT))) {
                    possibleCommands.add(Command.getName());
                }
            }
            return possibleCommands;
        }

        for (OnixCommandBase Command : commands) {
            if (!Command.getName().equalsIgnoreCase(args[0])) {
                continue;
            }
            if (Command.noConsole() && !(commandSender instanceof Player)) {
                return ImmutableList.of();
            }
            if (!Command.hasPermission(commandSender)) {
                return ImmutableList.of();
            }
            String[] processedArgs = this.processArgs(args);
            return Command.onTabComplete(commandSender, processedArgs);
        }
        return ImmutableList.of();
    }

    private String[] processArgs(String[] args) {
        String[] tempArgs = new String[args.length - 1];
        System.arraycopy(args, 1, tempArgs, 0, args.length - 1);
        return tempArgs;
    }

}
