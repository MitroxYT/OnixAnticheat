package me.onixdev.check.impl.combat.aim

import dev.onixac.api.check.CheckInfo
import dev.onixac.api.check.CheckStage
import dev.onixac.api.events.api.BaseEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.event.impl.PlayerRotationEvent
import me.onixdev.user.OnixUser
import kotlin.math.abs

@CheckInfo(name = "Aim", type = "A", stage = CheckStage.RELEASE, maxBuffer = 2.0, decayBuffer = 1.0)
class AimA(player: OnixUser?) : Check(player) {
    private var buffer = 0.0
    private var yawChange = 0f
    override fun onEvent(event: BaseEvent) {
        if (event is PlayerRotationEvent) {
            if (player.lastHitTime < 4 && !event.isPost) {
                val yawRate =
                    abs(player.rotationContainer.yaw - player.rotationContainer.lastYaw)
                        .toFloat() //Math.abs(player.xRot - player.lastXRot);
                if (yawRate < 1 || player.lastTeleportTime < 5) return
                if (yawRate == yawChange) {
                    if (++buffer > maxBuffer) {
                        fail("x=$yawRate, xc=$yawChange")
                    }
                } else if (buffer > 0) {
                    buffer -= 0.1
                }
                yawChange = yawRate
            }
        }
    }
}