package me.onixdev.events.bukkit

import me.onixdev.OnixAnticheat
import me.onixdev.event.impl.PlayerBlockBreakEvent
import me.onixdev.event.impl.PlayerBlockInteractEvent
import me.onixdev.event.impl.PlayerBlockPlaceEvent
import me.onixdev.util.extend.KotlinExtends.getData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

class PlayerBlockListener : Listener {
    @EventHandler
    fun onPlayerBreakBlock(event: BlockBreakEvent) {
        val user = event.player.getData()
        val eventBreak = PlayerBlockBreakEvent(event.block)
        user?.handleEvent(eventBreak)
        if (eventBreak.isCancelled) event.isCancelled = true
    }
    @EventHandler
    fun onPlayerInteractBlock(event: PlayerInteractEvent) {
        val user = event.player.getData()
        if (event.clickedBlock == null) return
        if (OnixAnticheat.INSTANCE.compatibilityManager.isLeafTicking) return
        val eventBreak = PlayerBlockInteractEvent(event.clickedBlock!!,event.action)
        user?.handleEvent(eventBreak)
        if (eventBreak.isCancelled) event.isCancelled = true
    }
    @EventHandler
    fun onPlayerBreakBlock(event: BlockPlaceEvent) {
        val user = event.player.getData()
        val eventBreak = PlayerBlockPlaceEvent(event.block)
        user?.handleEvent(eventBreak)
        if (eventBreak.isCancelled) event.isCancelled = true
    }
}