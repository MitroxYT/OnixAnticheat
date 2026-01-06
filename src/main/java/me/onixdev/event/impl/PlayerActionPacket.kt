package me.onixdev.event.impl

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction
import dev.onixac.api.events.api.BaseEvent

class PlayerActionPacket() : BaseEvent() {
   lateinit var action: WrapperPlayClientEntityAction.Action

    constructor(action: WrapperPlayClientEntityAction.Action) : this() {
        this.action = action
    }

}