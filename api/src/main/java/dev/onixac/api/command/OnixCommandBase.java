package dev.onixac.api.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class OnixCommandBase {

    protected final String name;

    public OnixCommandBase(String name) {
        this.name = name;
    }

    public abstract String getDescription();


    public String getUsage() {
        return "/" + "onix" + " " + getName();
    }

    public String getPermission() {
        return null;
    }

    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null || getPermission().isEmpty()) {
            return true;
        } else {
            return sender.hasPermission(getPermission());
        }
    }
    public List<String> getPlayersFirst(String names) {return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(names.toLowerCase())).collect(Collectors.toList());}
    public List<String> getPlayers() {return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new));}

    public abstract int getMinArgs();

    public abstract int getMaxArgs();

    public abstract boolean noConsole();

    public abstract boolean onCommand(@NotNull CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
    public String getName() {
        return this.name;
    }
}