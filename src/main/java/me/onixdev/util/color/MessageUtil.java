package me.onixdev.util.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class MessageUtil {
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
}
