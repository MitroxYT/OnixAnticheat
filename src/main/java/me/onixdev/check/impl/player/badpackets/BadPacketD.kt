package me.onixdev.check.impl.player.badpackets

import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.api.BaseEvent
import me.onixdev.event.impl.PlayerHeldItemChangeEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser

class BadPacketD(user:OnixUser) : Check(user, CheckBuilder.create().setCheckName("BadPacket").setType("D").build()) {

    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerHeldItemChangeEvent) {
            if (event.newSlot == event.oldSlot) {
                fail("slot: ${event.newSlot}")
                if (shouldCancel()) event.cancel()
            }
        }
    }
}