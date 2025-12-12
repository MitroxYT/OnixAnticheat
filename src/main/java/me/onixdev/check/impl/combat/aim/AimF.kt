package me.onixdev.check.impl.combat.aim

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

class AimF(player:OnixUser) : Check(player,CheckBuilder.create().setCheckName("Aim").setType("F").build()) {
    private var lastTickFlagget: Boolean = false
    override fun onEvent(event: BaseEvent?) {
        if (event is PlayerRotationEvent && !event.isPost && player.lastHitTime < 4) {
            var yawDiff: Float = abs(player.rotationContainer.lastYaw - player.rotationContainer.yaw).toFloat()
            if (yawDiff > 180.0f) {
                yawDiff = 360.0f - yawDiff
            }
            if (yawDiff > 85 && player.movementContainer.deltaXZ > 0.12) {
                if (lastTickFlagget) {
                    fail(String.format("%.5f", yawDiff))
                }
                lastTickFlagget = true
            }
            else lastTickFlagget = false
        }
    }
}