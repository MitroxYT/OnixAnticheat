package me.onixdev.check.impl.movement.noslow

import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

class NoslowPrediction(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Noslow").setType("A").build()) {
    private var lastTickNoslow = false
    private var buffer = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is TickEvent && event.tickType == TickEvent.Target.FLYING) {
            if (player.lastTeleportTime < 5) return
            if (player.isUsingItem && player.ItemUseTime > 3) {
                val offsetHorr = player.theoreticalInput.forwardMotion
                val offsetSt = player.theoreticalInput.strafe
                if (abs(offsetHorr) > 0.1970 || abs(offsetSt) > 0.1970) {
                    if (++buffer > 3) {
                        failAndSetback("of: $offsetHorr, strafe: $offsetHorr buf: $buffer")
                    }
                    if (player.ItemUseTime > 6 && !lastTickNoslow && player.movementContainer.deltaXZ > 0.09) {
                        player.getCheck(NoslowTick::class.java).failAndSetback("time: ${player.ItemUseTime}")
                    }
                    lastTickNoslow = true
                } else if (buffer > 0) {
                    buffer -= 0.5
                    lastTickNoslow = false
                }
            }
        }
    }
}