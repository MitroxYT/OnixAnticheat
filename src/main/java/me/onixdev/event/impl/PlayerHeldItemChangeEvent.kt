package me.onixdev.event.impl

import me.onixdev.event.api.BaseEvent

class PlayerHeldItemChangeEvent() : BaseEvent() {
    var newSlot : Int = 0
    var oldSlot : Int = 0
    constructor(newSlot: Int, oldSlot: Int) : this() {
        this.newSlot = newSlot
        this.oldSlot = oldSlot
    }
}