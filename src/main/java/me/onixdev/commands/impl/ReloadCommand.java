package me.onixdev.commands.impl;

import dev.onixac.api.command.OnixCommandBase;
import me.onixdev.OnixAnticheat;
import me.onixdev.check.api.Check;
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

    /**
     * @return
     */
    @Override
    public String getPermission() {
        return "onix.commands.reload";
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
