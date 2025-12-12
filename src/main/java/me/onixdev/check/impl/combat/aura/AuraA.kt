package me.onixdev.check.impl.combat.aura

import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerUseEntityEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser

class AuraA(player: OnixUser?) :
    Check(player,
        CheckBuilder.create().setCheckName("Aura").setType("A").setDescription("PostCheck")
            .setCheckStage(CheckStage.RELEASE).build()
    ) {
    private var send = false
    private var last: Long = 0
    override fun onEvent(event: BaseEvent) {
        if (event is PlayerUseEntityEvent) {
            send = true
        }
        if (event is TickEvent && event.tickType == TickEvent.Target.FLYING) {
            val delta = (System.currentTimeMillis() - last).toDouble()
            if (send) {
                if (delta > 40 && delta < 120) {
                    fail("d: $delta")
                }
                send = false
            }
            last = System.currentTimeMillis()
        }
    }
}