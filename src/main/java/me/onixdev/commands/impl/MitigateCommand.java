package me.onixdev.commands.impl;

import dev.onixac.api.command.OnixCommandBase;
import me.onixdev.OnixAnticheat;
import me.onixdev.user.OnixUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MitigateCommand extends OnixCommandBase {
    private final List<String> mitigates = Arrays.asList(
            "canceldamage",
            "reducedamage",
            "setback"
    );

    private final List<String> time = Arrays.asList(
            "1000", "2000", "3000", "5000", "10000"
    );

    public MitigateCommand() {
        super("mitigate");
    }

    @Override
    public String getPermission() {
        return "onix.mitigate";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 45;
    }

    @Override
    public boolean noConsole() {
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);

        if (target == null) {
            sender.sendMessage("§cИгрок не найден или оффлайн!");
            return false;
        }

        String mitigationType = args[1].toLowerCase();

        if (!mitigates.contains(mitigationType)) {
            return false;
        }

        double duration = 0;

        if (args.length == 3) {
            try {
                duration = Double.parseDouble(args[2]);
                if (duration < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        OnixUser targeted = OnixAnticheat.INSTANCE.getPlayerDatamanager().get(target.getUniqueId());
        if (targeted != null) {
            targeted.mitigate(mitigationType, duration);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());
        } else if (args.length == 2) {
            suggestions.addAll(mitigates.stream()
                    .filter(type -> type.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList());
        } else if (args.length == 3) {
            suggestions.addAll(time.stream()
                    .filter(time -> time.startsWith(args[2]))
                    .toList());
            if (suggestions.isEmpty()) {
                suggestions.add("<время в мс>");
            }
        }

        return suggestions;
    }

}
