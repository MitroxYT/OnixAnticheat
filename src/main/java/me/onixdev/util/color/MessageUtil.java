package me.onixdev.util.color;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import me.onixdev.OnixAnticheat;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {
    private static final Pattern Cp = Pattern.compile("(?i)" + '§' + "[0-9A-FK-ORX]");
    private static final Pattern HEX_PATTERN = Pattern.compile("([&§]#[A-Fa-f0-9]{6})|([&§]x([&§][A-Fa-f0-9]){6})");
    private static final BukkitAudiences adventure = BukkitAudiences.create(OnixAnticheat.INSTANCE.getPlugin());
    public static String translate(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        final Component component = MiniMessage.miniMessage().deserialize(input);
        return LegacyComponentSerializer.builder().character('§').hexColors().useUnusualXRepeatedCharacterHexFormat()
                .build().serialize(component).replace("&", "§");
    }
    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        String line = String.join("\n", list);
        return line;
    }
    public static @NotNull Component miniMessage(@NotNull String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        StringBuffer sb = new StringBuffer(string.length());

        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(0).replaceAll("[&§#x]", "") + ">");
        }

        matcher.appendTail(sb);
        string = sb.toString();
        string = translateAlternateColorCodes('&', string)
                .replace("§0", "<!b><!i><!u><!st><!obf><black>")
                .replace("§1", "<!b><!i><!u><!st><!obf><dark_blue>")
                .replace("§2", "<!b><!i><!u><!st><!obf><dark_green>")
                .replace("§3", "<!b><!i><!u><!st><!obf><dark_aqua>")
                .replace("§4", "<!b><!i><!u><!st><!obf><dark_red>")
                .replace("§5", "<!b><!i><!u><!st><!obf><dark_purple>")
                .replace("§6", "<!b><!i><!u><!st><!obf><gold>")
                .replace("§7", "<!b><!i><!u><!st><!obf><gray>")
                .replace("§8", "<!b><!i><!u><!st><!obf><dark_gray>")
                .replace("§9", "<!b><!i><!u><!st><!obf><blue>")
                .replace("§a", "<!b><!i><!u><!st><!obf><green>")
                .replace("§b", "<!b><!i><!u><!st><!obf><aqua>")
                .replace("§c", "<!b><!i><!u><!st><!obf><red>")
                .replace("§d", "<!b><!i><!u><!st><!obf><light_purple>")
                .replace("§e", "<!b><!i><!u><!st><!obf><yellow>")
                .replace("§f", "<!b><!i><!u><!st><!obf><white>")
                .replace("§r", "<reset>")
                .replace("§k", "<obfuscated>")
                .replace("§l", "<bold>")
                .replace("§m", "<strikethrough>")
                .replace("§n", "<underlined>")
                .replace("§o", "<italic>");

        return MiniMessage.miniMessage().deserialize(string).compact();
    }
    public static void sendMessage(@NotNull CommandSender commandSender, @NotNull Component component) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            FoliaScheduler.getEntityScheduler().run(
                    player,
                    OnixAnticheat.INSTANCE.getPlugin(),
                    t -> adventure.player(player).sendMessage(component),
                    () -> adventure.player(player).sendMessage(component)
            );
        } else {
            FoliaScheduler.getGlobalRegionScheduler().run(
                    OnixAnticheat.INSTANCE.getPlugin(),
                    (dummy) -> adventure.sender(commandSender).sendMessage(component)
            );
        }
    }

    @Contract("!null -> !null; null -> null")
    public static @Nullable String stripColor(@Nullable String input) {
        return input == null ? null : Cp.matcher(input).replaceAll("");
    }
    public static @NotNull String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }
}
