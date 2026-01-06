package me.onixdev.events.bukkit

import me.onixdev.OnixAnticheat
import me.onixdev.util.extend.KotlinExtends.getData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent


class PlayerReleaseUseItemState : Listener {
    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        val player = event.getPlayer()
        val user = player.getData()
        user?.setUsingItem(false)
    }
}
