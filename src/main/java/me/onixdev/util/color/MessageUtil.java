package me.onixdev.util.color;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class MessageUtil {
    private static final Pattern Cp = Pattern.compile("(?i)" + '§' + "[0-9A-FK-ORX]");
    public static String translate(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        final Component component = MiniMessage.miniMessage().deserialize(input);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        String line = String.join("\n", list);
        return line;
    }


    @Contract("!null -> !null; null -> null")
    public static @Nullable String stripColor(@Nullable String input) {
        return input == null ? null : Cp.matcher(input).replaceAll("");
    }
}
