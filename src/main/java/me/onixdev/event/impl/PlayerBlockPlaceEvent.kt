package me.onixdev.event.impl

import dev.onixac.api.events.api.BaseEvent
import org.bukkit.block.Block

class PlayerBlockPlaceEvent(private val block: Block) : BaseEvent() {
    fun getBlock() : Block {return block}
}