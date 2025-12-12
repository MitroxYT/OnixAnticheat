package me.onixdev.event.impl

import dev.onixac.api.events.api.BaseEvent

class PlayerUseEntityEvent() : BaseEvent() {
    var id:Int = 0

    constructor(use:UseType?, id:Int) : this() {
        this.useType = use
        this.id = id

    }
    var useType: UseType? = null


    enum class UseType {
        ATTACK,
        ATTACKBUKKIT,
        INTERACT
    }
}