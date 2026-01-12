package me.onixdev.commands.api;

import com.google.common.collect.ImmutableList;
import dev.onixac.api.command.OnixCommandBase;
import dev.onixac.api.manager.ICommandManager;
import me.onixdev.commands.impl.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CommandManager implements TabExecutor, ICommandManager {
    private final List<OnixCommandBase> commands = new ArrayList<>();
    //List.of(new VerboseCommands(),new ReloadCommand(),new MitigateCommand(),new AlertsCommand(),new DebugCommand(),new ProfileCommand());
    public CommandManager() {
        registerCommmand(new VerboseCommands());
        registerCommmand(new ReloadCommand());
        registerCommmand(new MitigateCommand());
        registerCommmand(new AlertsCommand());
        registerCommmand(new DebugCommand());
        registerCommmand(new ProfileCommand());
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

    /**
     * @param name
     * @return Возвращает комманду если не найдет
     * @since 1.0
     */
    @Override
    public Optional<OnixCommandBase> getCommand(String name) {
        for (OnixCommandBase command: commands) {
            if (command.getName().equals(name)) return Optional.of(command);
        }
        return Optional.empty();
    }

    /**
     * @param command
     * @since 1.0
     */
    @Override
    public void registerCommmand(OnixCommandBase command) {
        if (command == null || command.getName().isBlank()) return;
        commands.add(command);
    }
}
