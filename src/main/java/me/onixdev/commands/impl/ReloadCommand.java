package me.onixdev.commands.impl;

import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
import me.onixdev.commands.api.OnixCommandBase;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand extends OnixCommandBase {
    public ReloadCommand() {
        super("reload");
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
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        OnixAnticheat.INSTANCE.getReloadExecuter().run(()->{
            OnixAnticheat.INSTANCE.getConfigManager().reload();
            OnixAnticheat.INSTANCE.getPlayerDatamanager().getAllData().forEach(t->t.getChecks().forEach(Check::reload));
        });
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
