package me.onixdev.commands.impl;

import dev.onixac.api.command.OnixCommandBase;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.user.OnixUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChecksCommand extends OnixCommandBase {
    public ChecksCommand() {
        super("checks");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean noConsole() {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(player.getUniqueId());
            if (user == null) return true;
            List<String> a = new ArrayList<>();
            for (Check check : user.checks) {
                if (check.isEnabled()) a.add(check.getName() + " " + check.getType());
            }
            user.sendMessage("a" + a);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            OnixUser user = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(player.getUniqueId());
            if (user == null) return List.of();
            return user.getChecks().parallelStream().filter(Check::isEnabled).map(Check::getName).toList();
        }
        return List.of();
    }
}
