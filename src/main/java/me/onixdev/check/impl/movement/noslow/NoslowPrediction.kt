package me.onixdev.check.impl.movement.noslow

import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.api.BaseEvent
import me.onixdev.event.impl.TickEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

class NoslowPrediction(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Noslow").setType("A").build()) {
    private var lastTickNoslow = false
    private var buffer = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is TickEvent && event.tickType == TickEvent.Target.FLYING) {
            if (player.isUsingItem && player.ItemUseTime > 3) {
                val offsetHorr = player.theoreticalInput.forwardMotion
                val offsetSt = player.theoreticalInput.strafe
                if (abs(offsetHorr) > 0.1970 || abs(offsetSt) > 0.1970) {
                    //     if (lastTickNoslow) {
                    if (++buffer > 3) {
                        failAndSetback("of: $offsetHorr, strafe: $offsetHorr buf: $buffer")
                        //                            BukkitNMS.resetBukkitItemUsage(player);
                        //setback();
                    }
                    if (player.ItemUseTime > 6 && !lastTickNoslow) {
                        player.getCheck(NoslowTick::class.java).fail("time: ${player.ItemUseTime}")
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