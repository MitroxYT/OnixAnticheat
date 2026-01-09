package me.onixdev.event.impl

import dev.onixac.api.events.api.BaseEvent
import org.bukkit.block.Block
import org.bukkit.event.block.Action

class PlayerBlockInteractEvent(private val block: Block,private val action: Action) : BaseEvent() {
    fun getBlock() : Block {return block}
    fun getAction() : Action {return action}
}