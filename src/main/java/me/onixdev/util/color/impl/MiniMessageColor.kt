package me.onixdev.util.color.impl

import me.onixdev.util.color.Colorizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class MiniMessageColor : Colorizer {
    override fun colorize(message: String?): String? {
        if (message.isNullOrEmpty()) {
            return message
        }
        val component: Component = MiniMessage.miniMessage().deserialize(message)
        return LegacyComponentSerializer.builder().character('§').hexColors().useUnusualXRepeatedCharacterHexFormat()
            .build().serialize(component).replace("&", "§")
    }
}