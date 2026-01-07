package me.onixdev.util.extend

import com.github.retrooper.packetevents.protocol.player.User
import me.onixdev.OnixAnticheat
import me.onixdev.user.OnixUser
import org.bukkit.entity.Player

object KotlinExtends {
    fun Player.getData() : OnixUser? {
        val user = OnixAnticheat.INSTANCE.playerDatamanager.get(uniqueId) ?: return null
        return user
    }
    fun User.getData() : OnixUser? {
        val user = OnixAnticheat.INSTANCE.playerDatamanager.get(uuid) ?: return null
        return user
    }
}