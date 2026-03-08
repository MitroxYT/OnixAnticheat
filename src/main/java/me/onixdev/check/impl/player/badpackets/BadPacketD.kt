package me.onixdev.check.impl.player.badpackets

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.event.impl.PlayerHeldItemChangeEvent
import me.onixdev.user.OnixUser

@CheckInfo(name = "BadPacket", type = "D", stage = CheckStage.RELEASE, maxBuffer = 5.0, decayBuffer = 1.0)
class BadPacketD(user:OnixUser) : Check(user) {

    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerHeldItemChangeEvent) {
            if (event.newSlot == event.oldSlot) {
                fail("slot: ${event.newSlot}")
                if (shouldCancel()) event.cancel()
            }
        }
    }
}