package me.onixdev.event.impl

import dev.onixac.api.events.api.BaseEvent

class PlayerHeldItemChangeEvent() : BaseEvent() {
    var newSlot : Int = 0
    var oldSlot : Int = 0
    constructor(newSlot: Int, oldSlot: Int) : this() {
        this.newSlot = newSlot
        this.oldSlot = oldSlot
    }
}