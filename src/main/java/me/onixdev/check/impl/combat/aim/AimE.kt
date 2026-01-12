package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser

class AimE(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("Aim").setType("E").setBuffer(3.0).build()) {
    private var buffer:Double = 0.0
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && event.isPost) {
            if (player.lastHitTime < 4) {
                if (player.lastTeleportTime < 5) return
                val sens = player.sensitivity
                val valid = player.combatData.dist > 0.1
                if (sens < 0 && !player.rotationContainer.isCinematicRotation && valid) {
                    if (++buffer > maxBuffer) {
                        val form = String.format("%.5f", sens)
                        fail(form)
                    }
                }
                else if (buffer > 0) buffer-= decay

            }
        }
    }
}