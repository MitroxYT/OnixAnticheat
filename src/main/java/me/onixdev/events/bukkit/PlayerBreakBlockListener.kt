package me.onixdev.events.bukkit

import me.onixdev.event.impl.PlayerBlockBreakEvent
import me.onixdev.util.extend.KotlinExtends.getData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class PlayerBreakBlockListener : Listener {
    @EventHandler
    fun onPlayerBreakBlock(event: BlockBreakEvent) {
        val user = event.player.getData()
        val eventBreak = PlayerBlockBreakEvent(event.block)
        user?.handleEvent(eventBreak)
        if (eventBreak.isCancelled) event.isCancelled = true
    }
}