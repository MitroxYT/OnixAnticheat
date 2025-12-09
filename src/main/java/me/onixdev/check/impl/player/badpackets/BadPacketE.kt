package me.onixdev.check.impl.player.badpackets

import dev.onixac.api.check.CheckStage
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.event.impl.PlayerHeldItemChangeEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser

class BadPacketE(user: OnixUser) :
    Check(user, CheckBuilder.create().setCheckName("BadPacket").setCheckStage(CheckStage.BETA).setBuffer(4.0).setType("E").build()) {
    private var timeFromLastChange: Int = 41
    private var aboba: Int = 0
    override fun onEvent(event: BaseEvent?) {
        if (event is TickEvent) {
            timeFromLastChange++
        }
        if (event is PlayerHeldItemChangeEvent) {
            if (!event.isCancelled) {
                player.debug("newslot: ${event.newSlot} old: ${event.oldSlot} time $timeFromLastChange")
                if (timeFromLastChange == 0) {
                    if (++aboba > maxBuffer) {
                        fail("t: e=$timeFromLastChange p=$aboba")
                        if (shouldCancel()) event.cancel()
                    }
                }
                else if(aboba > 0) {
                    aboba-0.25
                }
            }
            //Спасибо моджанг что сделали когда афк пакет сендиться по идиотски
            if (player.theoreticalInput.hasInput()) timeFromLastChange = 0

        }
    }
}