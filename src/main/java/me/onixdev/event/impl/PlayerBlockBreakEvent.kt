package me.onixdev.event.impl

import dev.onixac.api.events.api.BaseEvent
import org.bukkit.block.Block

class PlayerBlockBreakEvent() : BaseEvent() {
    private  lateinit var block: Block
    constructor(block:Block):this() {
        this.block = block
    }
    fun getBlock(): Block = block
}