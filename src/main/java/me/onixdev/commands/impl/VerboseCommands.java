package me.onixdev.commands.impl;

import me.onixdev.OnixAnticheat;
import me.onixdev.commands.api.OnixCommandBase;
import me.onixdev.user.OnixUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VerboseCommands extends OnixCommandBase {
    public VerboseCommands() {
        super("verbose");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public boolean noConsole() {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        Player p = (Player) sender;
        OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(p.getUniqueId());
        if (user != null) {
            user.getAlertManager().toggleVerbose();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
